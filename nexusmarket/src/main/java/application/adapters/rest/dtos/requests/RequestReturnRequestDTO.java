package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Return request of a purchased variant of a delivered order. */
public record RequestReturnRequestDTO(
        @NotNull @Positive Long orderId,
        @NotNull @Positive Long variantId,
        @NotNull @Positive Integer quantity,
        @NotBlank @Size(max = 40) String reason,
        @Size(max = 500) String comment
) {
}
