package application.domain.services.shipping;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidShipmentException;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;

/**
 * Loads a shipment by its identifier or fails with EntityNotFoundException.
 */
public class FindShipmentService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public FindShipmentService(ShipmentRepositoryPort shipmentRepositoryPort) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    public Shipment execute(Shipment probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidShipmentException("Shipment identifier must be provided.");
        }
        return shipmentRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Shipment", probe.getId()));
    }
}
