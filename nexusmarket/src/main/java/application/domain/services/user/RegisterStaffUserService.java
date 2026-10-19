package application.domain.services.user;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
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
 * Creates a LOGISTIC_OPERATOR, SUPERVISOR or ADMIN user. Only an ADMIN can do it.
 */
public class RegisterStaffUserService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ValidatePasswordPolicyService validatePasswordPolicyService;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterStaffUserService(AuthorizeOperationService authorizeOperationService,
            ValidatePasswordPolicyService validatePasswordPolicyService,
            UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.validatePasswordPolicyService = validatePasswordPolicyService;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public User execute(User admin, User newUser) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        if (newUser == null) {
            throw new InvalidUserException("The new user must be provided.");
        }
        boolean staffRole = SystemRole.LOGISTIC_OPERATOR.equals(newUser.getRole())
                || SystemRole.SUPERVISOR.equals(newUser.getRole()) || SystemRole.ADMIN.equals(newUser.getRole());
        if (!staffRole) {
            throw new InvalidUserException("Only LOGISTIC_OPERATOR, SUPERVISOR or ADMIN users can be registered here.");
        }
        newUser.ensureIdentityValid();
        validatePasswordPolicyService.execute(newUser.getRawPassword());
        if (userRepositoryPort.existsByIdentification(newUser) || userRepositoryPort.existsByEmail(newUser)) {
            throw new DuplicateResourceException("Identification or e-mail already registered.");
        }
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setBuyer(null);
        newUser.setSeller(null);
        newUser.setPasswordHash(passwordServicePort.encrypt(newUser));
        newUser.setRawPassword(null);
        newUser.ensureRegistrationDataComplete();
        User saved = userRepositoryPort.save(newUser);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.USER_REGISTRATION, admin, saved, LocalDateTime.now(clock)),
                AuditDetails.of("role", saved.getRole().getCode(), "email", saved.getEmail()));
        return saved;
    }
}
