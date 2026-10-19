package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Editable fields of a warehouse. */
public record UpdateWarehouseRequestDTO(
        @NotBlank @Size(max = 120) String name,
        @NotNull @Valid AddressRequestDTO address
) {
}
