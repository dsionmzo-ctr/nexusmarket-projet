package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Entry of the inventory movement history. */
public record InventoryMovementResponseDTO(
        Long id,
        Long inventoryId,
        String type,
        int quantity,
        int onHandAfter,
        Long performedById,
        LocalDateTime occurredAt,
        String note
) {
}
