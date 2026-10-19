package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** New status (catalog code) of a user, buyer or warehouse. */
public record ChangeStatusRequestDTO(
        @NotBlank @Size(max = 40) String status
) {
}
