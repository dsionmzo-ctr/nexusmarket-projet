package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payment method used to pay an order. */
public record PayOrderRequestDTO(
        @NotBlank @Size(max = 40) String paymentMethod
) {
}
