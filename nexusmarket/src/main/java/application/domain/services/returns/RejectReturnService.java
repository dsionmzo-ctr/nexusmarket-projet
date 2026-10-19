package application.domain.services.returns;

import application.domain.models.Operation;
import application.domain.models.ProductReturn;
import application.domain.models.User;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A LOGISTIC_OPERATOR rejects a return (goods not accepted). The refund opened with it is rejected too.
 */
public class RejectReturnService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindProductReturnService findProductReturnService;
    private final ProductReturnRepositoryPort productReturnRepositoryPort;
    private final RefundRepositoryPort refundRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RejectReturnService(AuthorizeOperationService authorizeOperationService,
            FindProductReturnService findProductReturnService,
            ProductReturnRepositoryPort productReturnRepositoryPort,
            RefundRepositoryPort refundRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findProductReturnService = findProductReturnService;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
        this.refundRepositoryPort = refundRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public ProductReturn execute(User user, ProductReturn requested) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        ProductReturn productReturn = findProductReturnService.execute(requested);
        LocalDateTime now = LocalDateTime.now(clock);
        productReturn.reject();
        productReturnRepositoryPort.update(productReturn);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.RETURN_REJECTION, user, productReturn, now),
                AuditDetails.of("orderId", productReturn.getOrder().getId()));
        refundRepositoryPort.findByReturn(productReturn).ifPresent(refund -> {
            refund.reject(user, "Return rejected", now);
            refundRepositoryPort.update(refund);
            registerOperationAndAuditService.execute(
                    Operation.of(OperationType.REFUND_REJECTION, user, refund, now),
                    AuditDetails.of("reason", "Return rejected"));
        });
        return productReturn;
    }
}
