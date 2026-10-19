package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Shipment of the physical items of an order. */
public record ShipmentResponseDTO(
        Long id,
        Long orderId,
        String trackingNumber,
        AddressResponseDTO destination,
        String status,
        LocalDateTime createdAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        Long handledById
) {
}
