package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.ShipmentResponseDTO;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;

/** Shipment Domain Model <-> shipment DTO and query filter. */
public final class ShipmentRestMapper {

    private ShipmentRestMapper() {
    }

    /** Filter by status; a null or blank status means "all shipments". */
    public static Shipment toFilter(String statusCode) {
        Shipment filter = new Shipment();
        if (statusCode != null && !statusCode.isBlank()) {
            filter.setStatus(ShipmentStatus.fromCode(statusCode));
        }
        return filter;
    }

    public static Shipment toProbe(Long shipmentId) {
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);
        return shipment;
    }

    public static ShipmentResponseDTO toResponse(Shipment shipment) {
        return new ShipmentResponseDTO(shipment.getId(), shipment.getOrder().getId(), shipment.getTrackingNumber(),
                AddressRestMapper.toResponse(shipment.getDestination()), shipment.getStatus().getCode(),
                shipment.getCreatedAt(), shipment.getShippedAt(), shipment.getDeliveredAt(),
                shipment.getHandledBy() == null ? null : shipment.getHandledBy().getId());
    }
}
