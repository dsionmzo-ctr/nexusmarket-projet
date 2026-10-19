package application.adapters.rest.dtos.responses;

/** Public data of a user (never the password). */
public record UserResponseDTO(
        Long id,
        String identification,
        String name,
        String email,
        String role,
        String status
) {
}
