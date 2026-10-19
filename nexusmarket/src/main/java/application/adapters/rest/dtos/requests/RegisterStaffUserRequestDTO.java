package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Creation of a LOGISTIC_OPERATOR, SUPERVISOR or ADMIN user, by an admin. */
public record RegisterStaffUserRequestDTO(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)?$", message = "must not have surrounding whitespace") String identification,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Pattern(regexp = "LOGISTIC_OPERATOR|SUPERVISOR|ADMIN", message = "must be LOGISTIC_OPERATOR, SUPERVISOR or ADMIN") String role
) {
}
