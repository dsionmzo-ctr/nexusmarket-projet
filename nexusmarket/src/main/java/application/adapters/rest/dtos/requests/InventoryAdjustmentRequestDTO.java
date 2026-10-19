package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Adjustment of the on-hand units of an inventory record to the counted quantity. */
public record InventoryAdjustmentRequestDTO(
        @NotNull @PositiveOrZero Integer newQuantity,
        @NotBlank @Size(max = 500) String note
) {
}
