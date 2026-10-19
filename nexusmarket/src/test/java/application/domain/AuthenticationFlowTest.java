package application.domain;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.services.user.LoadAuthenticatedUserService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.support.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Users domain: registration, login, logout, token version and uniqueness rules (RG-01, RG-02, section 11). */
class AuthenticationFlowTest {

    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenario = new Scenario();
    }

    @Test
    void buyerUserIsCreatedWithBuyerRoleActiveStatusAndHashedPassword() {
        User user = scenario.buyerUser;
        assertEquals(SystemRole.BUYER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("hash:" + Scenario.PASSWORD, user.getPasswordHash());
        assertNull(user.getRawPassword());
        assertNotNull(user.getBuyer());
    }

    @Test
    void loginReturnsTokenAndUserWithoutRawPassword() {
        AuthenticationResult result = scenario.publicAccess.login(Scenario.credentials("buyer@nexus.com", Scenario.PASSWORD));
        assertEquals("token-" + scenario.buyerUser.getId() + "-v1", result.getToken());
        assertEquals(3600, result.getExpiresIn());
        assertNull(result.getAuthenticatedUser().getRawPassword());
    }

    @Test
    void unknownEmailAndWrongPasswordAreIndistinguishable() {
        InvalidCredentialsException unknown = assertThrows(InvalidCredentialsException.class,
                () -> scenario.publicAccess.login(Scenario.credentials("nobody@nexus.com", Scenario.PASSWORD)));
        InvalidCredentialsException wrong = assertThrows(InvalidCredentialsException.class,
                () -> scenario.publicAccess.login(Scenario.credentials("buyer@nexus.com", "WrongPassword1")));
        assertEquals(unknown.getMessage(), wrong.getMessage());
    }

    @Test
    void blockedUserCannotLogIn() {
        scenario.adminPort.changeUserStatus(scenario.admin, scenario.buyerUser, UserStatus.BLOCKED);
        assertThrows(UnauthorizedOperationException.class,
                () -> scenario.publicAccess.login(Scenario.credentials("buyer@nexus.com", Scenario.PASSWORD)));
    }

    @Test
    void logoutInvalidatesPreviouslyIssuedTokens() {
        LoadAuthenticatedUserService loader = scenario.container.get(LoadAuthenticatedUserService.class);
        AuthenticationResult login = scenario.publicAccess.login(Scenario.credentials("buyer@nexus.com", Scenario.PASSWORD));
        long issuedVersion = login.getAuthenticatedUser().getAuthTokenVersion();
        assertNotNull(loader.execute(scenario.buyerUser.getId(), issuedVersion));

        scenario.publicAccess.logout(login.getAuthenticatedUser());

        assertThrows(UnauthorizedOperationException.class, () -> loader.execute(scenario.buyerUser.getId(), issuedVersion));
        assertNotNull(loader.execute(scenario.buyerUser.getId(), issuedVersion + 1));
    }

    @Test
    void statusChangeAlsoInvalidatesTokensAndInactiveUsersAreRejected() {
        LoadAuthenticatedUserService loader = scenario.container.get(LoadAuthenticatedUserService.class);
        scenario.adminPort.changeUserStatus(scenario.admin, scenario.buyerUser, UserStatus.INACTIVE);
        User stored = scenario.container.users.findById(scenario.buyerUser).orElseThrow();
        assertFalse(stored.isActive());
        assertThrows(UnauthorizedOperationException.class, () -> loader.execute(stored.getId(), stored.getAuthTokenVersion()));
    }

    @Test
    void anonymousLogoutIsANoOp() {
        scenario.publicAccess.logout(null);
        scenario.publicAccess.logout(new User());
    }

    @Test
    void identificationAndEmailMustBeUniqueAcrossTheMarketplace() {
        Buyer duplicate = new Buyer();
        duplicate.setIdentification("1017123456");
        duplicate.setName("Other Person");
        duplicate.setEmail("another@nexus.com");
        duplicate.setPrimaryAddress(new Address("Street 1", "City", "Country"));
        assertThrows(DuplicateResourceException.class, () -> scenario.publicAccess.registerBuyer(duplicate));

        Buyer sameEmail = new Buyer();
        sameEmail.setIdentification("5555555");
        sameEmail.setName("Other Person");
        sameEmail.setEmail("BUYER@nexus.com");
        sameEmail.setPrimaryAddress(new Address("Street 1", "City", "Country"));
        assertThrows(DuplicateResourceException.class, () -> scenario.publicAccess.registerBuyer(sameEmail));
    }

    @Test
    void buyerEmailCannotCollideWithAStaffUser() {
        Buyer collision = new Buyer();
        collision.setIdentification("7777777");
        collision.setName("Collision");
        collision.setEmail("admin@nexus.com");
        collision.setPrimaryAddress(new Address("Street 1", "City", "Country"));
        assertThrows(DuplicateResourceException.class, () -> scenario.publicAccess.registerBuyer(collision));
    }

    @Test
    void weakPasswordsAreRejected() {
        Buyer buyer = new Buyer();
        buyer.setIdentification("8888888");
        buyer.setName("Weak Password");
        buyer.setEmail("weak@nexus.com");
        buyer.setPrimaryAddress(new Address("Street 1", "City", "Country"));
        Buyer saved = scenario.publicAccess.registerBuyer(buyer);
        User account = new User();
        Buyer reference = new Buyer();
        reference.setIdentification(saved.getIdentification());
        account.setBuyer(reference);
        account.setRawPassword("onlyletters");
        assertThrows(InvalidUserException.class, () -> scenario.publicAccess.registerBuyerUser(account));
    }

    @Test
    void onlyAdminsRegisterStaffUsers() {
        User newStaff = Scenario.staff("123", "Intruder", "intruder@nexus.com", SystemRole.ADMIN);
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.registerStaffUser(scenario.buyerUser, newStaff));
        assertTrue(scenario.container.users.findAll().stream().noneMatch(u -> u.getEmail().equals("intruder@nexus.com")));
    }

    @Test
    void staffRegistrationOnlyAcceptsStaffRoles() {
        User asBuyer = Scenario.staff("124", "Fake Staff", "fake@nexus.com", SystemRole.BUYER);
        assertThrows(InvalidUserException.class, () -> scenario.adminPort.registerStaffUser(scenario.admin, asBuyer));
    }
}
