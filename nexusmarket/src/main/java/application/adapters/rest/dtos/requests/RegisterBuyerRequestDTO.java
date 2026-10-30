package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Self-registration of a buyer profile and its access account (POST /api/v1/auth/register/buyer). */
public record RegisterBuyerRequestDTO(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)?$", message = "must not have surrounding whitespace") String identification,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull @Valid AddressRequestDTO primaryAddress
) {
}
