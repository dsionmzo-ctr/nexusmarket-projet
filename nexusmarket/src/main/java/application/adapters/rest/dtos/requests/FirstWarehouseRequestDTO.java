package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** First warehouse registered together with a new seller. */
public record FirstWarehouseRequestDTO(
        @NotBlank @Size(max = 120) String name,
        @NotNull @Valid AddressRequestDTO address
) {
}
