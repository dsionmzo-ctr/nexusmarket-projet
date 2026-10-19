package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Warehouse that receives the returned goods. */
public record ReceiveReturnRequestDTO(
        @NotNull @Positive Long warehouseId
) {
}
