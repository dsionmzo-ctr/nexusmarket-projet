package application.domain.services.seller;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.Operation;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Incorporates a seller. Sellers cannot self-register: only an ADMIN can do it. The identification and the
 * e-mail must be unique in the platform.
 */
public class RegisterSellerService {

    private final AuthorizeOperationService authorizeOperationService;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterSellerService(AuthorizeOperationService authorizeOperationService,
            SellerRepositoryPort sellerRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Seller execute(User admin, Seller seller) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        if (seller == null) {
            throw new InvalidUserException("Seller must be provided.");
        }
        seller.setRole(SystemRole.SELLER);
        seller.ensureComplete();
        User probe = new User();
        probe.setIdentification(seller.getIdentification());
        probe.setEmail(seller.getEmail());
        if (sellerRepositoryPort.existsByIdentification(seller) || sellerRepositoryPort.existsByEmail(seller)
                || userRepositoryPort.existsByIdentification(probe) || userRepositoryPort.existsByEmail(probe)) {
            throw new DuplicateResourceException("Identification or e-mail already registered.");
        }
        Seller saved = sellerRepositoryPort.save(seller);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.SELLER_REGISTRATION, admin, saved, LocalDateTime.now(clock)),
                AuditDetails.of("businessName", saved.getBusinessName(), "email", saved.getEmail()));
        return saved;
    }
}
