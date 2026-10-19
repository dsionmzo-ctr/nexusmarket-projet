package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Inflow of units of a variant into a warehouse. */
public record InventoryInflowRequestDTO(
        @NotNull @Positive Long variantId,
        @NotNull @Positive Long warehouseId,
        @NotNull @Positive Integer quantity,
        @Size(max = 500) String note
) {
}
