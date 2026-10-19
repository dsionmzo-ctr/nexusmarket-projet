package application.domain.models;

import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * System identity used for authentication and authorization. Inherits identity and role from
 * {@link Person}. A user may represent a buyer or a seller (staff users have neither).
 *
 * The password is only stored hashed ({@code passwordHash}); {@code rawPassword} is transient and
 * only travels from the REST boundary to the password service. {@code authTokenVersion} is bumped
 * on every security-relevant change so previously issued tokens become obsolete.
 */
public class User extends Person {

    private String passwordHash;
    private String rawPassword;
    private UserStatus status = UserStatus.ACTIVE;
    private long authTokenVersion = 1L;
    private Buyer buyer;
    private Seller seller;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public long getAuthTokenVersion() {
        return authTokenVersion;
    }

    public void setAuthTokenVersion(long authTokenVersion) {
        this.authTokenVersion = authTokenVersion;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }


    @Override
    public String getResourceType() {
        return "USER";
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }

    public boolean hasRole(SystemRole expected) {
        return getRole() != null && getRole().equals(expected);
    }

    /** Changes the access status and invalidates every previously issued token. */
    public void changeStatus(UserStatus target) {
        if (target == null || target.equals(status)) {
            throw new InvalidStatusTransitionException("User", status, target);
        }
        this.status = target;
        bumpAuthTokenVersion();
    }

    public void bumpAuthTokenVersion() {
        this.authTokenVersion = authTokenVersion + 1;
    }

    public void changePasswordHash(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new InvalidUserException("Password must be provided.");
        }
        this.passwordHash = newPasswordHash;
        bumpAuthTokenVersion();
    }

    /** Guarantees the mandatory attributes of a user before it is persisted. */
    public void ensureRegistrationDataComplete() {
        ensureIdentityValid();
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new InvalidUserException("Password must be provided.");
        }
    }

    /** RG-01: every operation must be executed by an authenticated, active user. */
    public void requireActive() {
        if (!isActive()) {
            throw new UnauthorizedOperationException("User " + getEmail() + " is not active.");
        }
    }

}
