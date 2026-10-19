package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** New quantity of a cart line. */
public record UpdateCartItemRequestDTO(
        @NotNull @Positive Integer quantity
) {
}
