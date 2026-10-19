package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Administrative incorporation of a seller and its first warehouse. */
public record RegisterSellerRequestDTO(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)?$", message = "must not have surrounding whitespace") String identification,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 160) String businessName,
        @NotNull @Valid FirstWarehouseRequestDTO firstWarehouse
) {
}
