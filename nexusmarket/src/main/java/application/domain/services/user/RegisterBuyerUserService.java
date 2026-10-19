package application.domain.services.user;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Buyer;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Creates the system user (credentials) of an already registered buyer. The identity and role are taken
 * from the buyer; the e-mail and identification must be unique in the platform.
 */
public class RegisterBuyerUserService {

    private final ValidatePasswordPolicyService validatePasswordPolicyService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterBuyerUserService(ValidatePasswordPolicyService validatePasswordPolicyService,
            BuyerRepositoryPort buyerRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.validatePasswordPolicyService = validatePasswordPolicyService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public User execute(User user) {
        if (user == null || user.getBuyer() == null || user.getBuyer().getIdentification() == null) {
            throw new InvalidUserException("The identification of the buyer must be provided.");
        }
        validatePasswordPolicyService.execute(user.getRawPassword());
        Buyer buyer = buyerRepositoryPort.findByIdentification(user.getBuyer())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", user.getBuyer().getIdentification()));
        user.setBuyer(buyer);
        user.setIdentification(buyer.getIdentification());
        user.setName(buyer.getName());
        user.setEmail(buyer.getEmail());
        user.setRole(SystemRole.BUYER);
        user.setStatus(UserStatus.ACTIVE);
        if (userRepositoryPort.existsByIdentification(user) || userRepositoryPort.existsByEmail(user)) {
            throw new DuplicateResourceException("A user already exists for the buyer " + buyer.getIdentification());
        }
        user.setPasswordHash(passwordServicePort.encrypt(user));
        user.setRawPassword(null);
        user.ensureRegistrationDataComplete();
        User saved = userRepositoryPort.save(user);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.BUYER_REGISTRATION, saved, buyer, LocalDateTime.now(clock)),
                AuditDetails.of("userId", saved.getId(), "email", saved.getEmail()));
        return saved;
    }
}
