package application.adapters.rest.dtos.responses;

import java.util.Map;

/** Variant of a product. */
public record ProductVariantResponseDTO(
        Long id,
        String sku,
        Map<String, String> attributes
) {
}
