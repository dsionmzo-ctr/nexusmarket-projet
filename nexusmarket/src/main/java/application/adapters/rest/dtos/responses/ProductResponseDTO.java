package application.adapters.rest.dtos.responses;

import java.util.List;

/** Product with its variants. */
public record ProductResponseDTO(
        Long id,
        Long sellerId,
        String sellerBusinessName,
        String name,
        String description,
        String type,
        String status,
        MoneyResponseDTO price,
        List<ProductVariantResponseDTO> variants
) {
}
