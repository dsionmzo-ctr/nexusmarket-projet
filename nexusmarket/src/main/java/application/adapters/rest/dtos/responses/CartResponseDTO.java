package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;

/** Shopping cart of the buyer. */
public record CartResponseDTO(
        Long id,
        List<CartItemResponseDTO> items,
        MoneyResponseDTO total,
        LocalDateTime updatedAt
) {
}
