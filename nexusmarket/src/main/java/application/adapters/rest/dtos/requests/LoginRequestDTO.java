package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Credentials of POST /api/v1/auth/login. Unknown e-mail and wrong password answer the same 401. */
public record LoginRequestDTO(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
