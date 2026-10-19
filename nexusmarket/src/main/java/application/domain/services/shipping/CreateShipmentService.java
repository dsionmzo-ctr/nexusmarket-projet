package application.domain.services.shipping;

import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Opens the shipment (PREPARING) of a paid order with physical items.
 */
public class CreateShipmentService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public CreateShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Shipment execute(User actor, Order order) {
        LocalDateTime now = LocalDateTime.now(clock);
        String tracking = "NXM-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Shipment saved = shipmentRepositoryPort.save(Shipment.prepareFor(order, tracking, now));
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.SHIPMENT_CREATION, actor, saved, now),
                AuditDetails.of("orderId", order.getId(), "tracking", tracking));
        return saved;
    }
}
