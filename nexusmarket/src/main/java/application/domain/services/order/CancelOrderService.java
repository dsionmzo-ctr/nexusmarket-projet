package application.domain.services.order;

import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.inventory.ReleaseStockService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Cancels an unpaid order of the authenticated buyer and releases its reserved stock.
 */
public class CancelOrderService {

    private final FindBuyerOrderService findBuyerOrderService;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ReleaseStockService releaseStockService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public CancelOrderService(FindBuyerOrderService findBuyerOrderService,
            OrderRepositoryPort orderRepositoryPort,
            ReleaseStockService releaseStockService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findBuyerOrderService = findBuyerOrderService;
        this.orderRepositoryPort = orderRepositoryPort;
        this.releaseStockService = releaseStockService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Order execute(User user, Order requested) {
        Order order = findBuyerOrderService.execute(user, requested);
        order.cancel();
        releaseStockService.execute(user, order);
        orderRepositoryPort.update(order);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.ORDER_CANCELLATION, user, order, LocalDateTime.now(clock)),
                AuditDetails.of("total", order.total().amount().toPlainString()));
        return order;
    }
}
