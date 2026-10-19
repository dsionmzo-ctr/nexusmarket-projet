package application.domain.services.user;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Operation;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Creates the system user (credentials) of an already registered seller. Only an ADMIN can do it.
 */
public class RegisterSellerUserService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ValidatePasswordPolicyService validatePasswordPolicyService;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterSellerUserService(AuthorizeOperationService authorizeOperationService,
            ValidatePasswordPolicyService validatePasswordPolicyService,
            SellerRepositoryPort sellerRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.validatePasswordPolicyService = validatePasswordPolicyService;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public User execute(User admin, User sellerUser) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        if (sellerUser == null || sellerUser.getSeller() == null || sellerUser.getSeller().getIdentification() == null) {
            throw new InvalidUserException("The identification of the seller must be provided.");
        }
        validatePasswordPolicyService.execute(sellerUser.getRawPassword());
        Seller seller = sellerRepositoryPort.findByIdentification(sellerUser.getSeller())
                .orElseThrow(() -> new EntityNotFoundException("Seller", sellerUser.getSeller().getIdentification()));
        sellerUser.setSeller(seller);
        sellerUser.setIdentification(seller.getIdentification());
        sellerUser.setName(seller.getName());
        sellerUser.setEmail(seller.getEmail());
        sellerUser.setRole(SystemRole.SELLER);
        sellerUser.setStatus(UserStatus.ACTIVE);
        if (userRepositoryPort.existsByIdentification(sellerUser) || userRepositoryPort.existsByEmail(sellerUser)) {
            throw new DuplicateResourceException("A user already exists for the seller " + seller.getIdentification());
        }
        sellerUser.setPasswordHash(passwordServicePort.encrypt(sellerUser));
        sellerUser.setRawPassword(null);
        sellerUser.ensureRegistrationDataComplete();
        User saved = userRepositoryPort.save(sellerUser);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.USER_REGISTRATION, admin, saved, LocalDateTime.now(clock)),
                AuditDetails.of("role", saved.getRole().getCode(), "email", saved.getEmail()));
        return saved;
    }
}
