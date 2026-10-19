package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Creation of a warehouse. sellerId is required for SELLER warehouses. */
public record CreateWarehouseRequestDTO(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 40) String type,
        @NotNull @Valid AddressRequestDTO address,
        @Positive Long sellerId
) {
}
