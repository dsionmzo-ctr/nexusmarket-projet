package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Creation of the access account of an already registered buyer (POST /api/v1/auth/register/user). */
public record RegisterUserRequestDTO(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)?$", message = "must not have surrounding whitespace") String buyerIdentification,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
