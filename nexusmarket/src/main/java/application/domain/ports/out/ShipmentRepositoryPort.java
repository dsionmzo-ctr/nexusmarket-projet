package application.domain.ports.out;

import application.domain.models.Order;
import application.domain.models.Shipment;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of shipments.
 */
public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);

    Optional<Shipment> findById(Shipment shipment);

    Optional<Shipment> findByOrder(Order order);

    /** Shipments with the status of the supplied model, or every shipment when its status is null. */
    List<Shipment> findByStatus(Shipment shipment);

    void update(Shipment shipment);
}
