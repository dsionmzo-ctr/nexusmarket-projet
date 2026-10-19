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
 * A LOGISTIC_OPERATOR dispatches a shipment (physical exit from the warehouse). The order becomes SHIPPED.
 */
public class DispatchShipmentService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindShipmentService findShipmentService;
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final NotifyPersonService notifyPersonService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public DispatchShipmentService(AuthorizeOperationService authorizeOperationService,
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
        shipment.dispatch(user, now);
        order.markShipped();
        orderRepositoryPort.update(order);
        shipmentRepositoryPort.update(shipment);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.SHIPMENT_DISPATCH, user, shipment, now),
                AuditDetails.of("orderId", order.getId(), "tracking", shipment.getTrackingNumber()));
        notifyPersonService.execute(order.getBuyer(), "Order shipped",
                "Your order " + order.getId() + " is on its way. Tracking: " + shipment.getTrackingNumber());
        return shipment;
    }
}
