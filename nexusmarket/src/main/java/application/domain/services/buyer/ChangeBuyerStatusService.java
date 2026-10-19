package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Changes the commercial status of a buyer (ENABLED, RESTRICTED). Only an ADMIN can do it.
 */
public class ChangeBuyerStatusService {

    private final AuthorizeOperationService authorizeOperationService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ChangeBuyerStatusService(AuthorizeOperationService authorizeOperationService,
            BuyerRepositoryPort buyerRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Buyer execute(User admin, Buyer buyer, BuyerStatus newStatus) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        Buyer stored = buyerRepositoryPort.findById(buyer)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", buyer.getId()));
        BuyerStatus previous = stored.getStatus();
        stored.changeStatus(newStatus);
        buyerRepositoryPort.update(stored);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.BUYER_STATUS_CHANGE, admin, stored, LocalDateTime.now(clock)),
                AuditDetails.of("previousStatus", previous.getCode(), "newStatus", newStatus.getCode()));
        return stored;
    }
}
