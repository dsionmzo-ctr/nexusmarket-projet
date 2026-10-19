package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Size;

/** Optional note when marking an inventory record as damaged or available again. */
public record InventoryNoteRequestDTO(
        @Size(max = 500) String note
) {
}
