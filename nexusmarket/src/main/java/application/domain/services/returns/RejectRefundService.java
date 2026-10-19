package application.domain.services.returns;

import application.domain.models.Operation;
import application.domain.models.ProductReturn;
import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.notification.NotifyPersonService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * The ADMIN rejects a refund. The decision records who decided and when.
 */
public class RejectRefundService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindRefundService findRefundService;
    private final FindProductReturnService findProductReturnService;
    private final RefundRepositoryPort refundRepositoryPort;
    private final NotifyPersonService notifyPersonService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RejectRefundService(AuthorizeOperationService authorizeOperationService,
            FindRefundService findRefundService,
            FindProductReturnService findProductReturnService,
            RefundRepositoryPort refundRepositoryPort,
            NotifyPersonService notifyPersonService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findRefundService = findRefundService;
        this.findProductReturnService = findProductReturnService;
        this.refundRepositoryPort = refundRepositoryPort;
        this.notifyPersonService = notifyPersonService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Refund execute(User admin, Refund requested) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        Refund refund = findRefundService.execute(requested);
        ProductReturn productReturn = findProductReturnService.execute(refund.getProductReturn());
        LocalDateTime now = LocalDateTime.now(clock);
        refund.reject(admin, requested.getDecisionNote(), now);
        refundRepositoryPort.update(refund);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.REFUND_REJECTION, admin, refund, now),
                AuditDetails.of("orderId", productReturn.getOrder().getId(), "note", requested.getDecisionNote()));
        notifyPersonService.execute(productReturn.getOrder().getBuyer(), "Refund rejected",
                "The refund " + refund.getId() + " was rejected.");
        return refund;
    }
}
