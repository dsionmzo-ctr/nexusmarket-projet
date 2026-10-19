package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Creation of the access account of a seller, by an admin. */
public record RegisterSellerUserRequestDTO(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)?$", message = "must not have surrounding whitespace") String sellerIdentification,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
