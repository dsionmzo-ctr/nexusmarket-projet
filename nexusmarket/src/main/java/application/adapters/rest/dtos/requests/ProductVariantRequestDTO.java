package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/** Variant of a product (color, size, model, ...). */
public record ProductVariantRequestDTO(
        @NotBlank @Size(max = 80) String sku,
        Map<String, String> attributes
) {
}
