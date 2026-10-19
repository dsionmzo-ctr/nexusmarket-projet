package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Variant and quantity to add to the cart. */
public record CartItemRequestDTO(
        @NotNull @Positive Long variantId,
        @NotNull @Positive Integer quantity
) {
}
