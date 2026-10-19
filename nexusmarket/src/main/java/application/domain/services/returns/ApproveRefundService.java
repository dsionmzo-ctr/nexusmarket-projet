package application.domain.services.returns;

import application.domain.exceptions.DomainException;
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
 * The ADMIN approves a refund. The returned goods must have been received first. The decision records who
 * decided and when.
 */
public class ApproveRefundService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindRefundService findRefundService;
    private final FindProductReturnService findProductReturnService;
    private final RefundRepositoryPort refundRepositoryPort;
    private final NotifyPersonService notifyPersonService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ApproveRefundService(AuthorizeOperationService authorizeOperationService,
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
        if (!productReturn.isReceived()) {
            throw new DomainException("A refund can only be approved after the returned goods are received.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        refund.approve(admin, requested.getDecisionNote(), now);
        refundRepositoryPort.update(refund);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.REFUND_APPROVAL, admin, refund, now),
                AuditDetails.of("amount", refund.getAmount().amount().toPlainString(),
                        "orderId", productReturn.getOrder().getId(), "note", requested.getDecisionNote()));
        notifyPersonService.execute(productReturn.getOrder().getBuyer(), "Refund approved",
                "The refund " + refund.getId() + " was approved.");
        return refund;
    }
}
