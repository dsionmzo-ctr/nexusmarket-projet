package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.order.FindBuyerOrderService;

/**
 * Consults the shipment of an order of the authenticated buyer.
 */
public class ConsultOrderShipmentService {

    private final FindBuyerOrderService findBuyerOrderService;
    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public ConsultOrderShipmentService(FindBuyerOrderService findBuyerOrderService,
            ShipmentRepositoryPort shipmentRepositoryPort) {
        this.findBuyerOrderService = findBuyerOrderService;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    public Shipment execute(User user, Order probe) {
        Order order = findBuyerOrderService.execute(user, probe);
        return shipmentRepositoryPort.findByOrder(order)
                .orElseThrow(() -> new EntityNotFoundException("Shipment of order", order.getId()));
    }
}
