package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Return of purchased goods. */
public record ProductReturnResponseDTO(
        Long id,
        Long orderId,
        Long variantId,
        String sku,
        String productName,
        int quantity,
        String reason,
        String comment,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime receivedAt,
        Long receivedWarehouseId
) {
}
