package application.domain.services.shipping;

import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.notification.NotifyPersonService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A LOGISTIC_OPERATOR confirms the delivery of a shipment. The order becomes DELIVERED (finalized).
 */
public class ConfirmShipmentDeliveryService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindShipmentService findShipmentService;
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final NotifyPersonService notifyPersonService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ConfirmShipmentDeliveryService(AuthorizeOperationService authorizeOperationService,
            FindShipmentService findShipmentService,
            ShipmentRepositoryPort shipmentRepositoryPort,
            OrderRepositoryPort orderRepositoryPort,
            NotifyPersonService notifyPersonService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findShipmentService = findShipmentService;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.notifyPersonService = notifyPersonService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Shipment execute(User user, Shipment probe) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        Shipment shipment = findShipmentService.execute(probe);
        Order order = shipment.getOrder();
        LocalDateTime now = LocalDateTime.now(clock);
        shipment.confirmDelivery(user, now);
        order.markDelivered(now);
        orderRepositoryPort.update(order);
        shipmentRepositoryPort.update(shipment);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.SHIPMENT_DELIVERY, user, shipment, now),
                AuditDetails.of("orderId", order.getId()));
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.ORDER_DELIVERY, user, order, now),
                AuditDetails.of("shipmentId", shipment.getId()));
        notifyPersonService.execute(order.getBuyer(), "Order delivered",
                "Your order " + order.getId() + " was delivered.");
        return shipment;
    }
}
