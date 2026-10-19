package application.adapters.rest.dtos.responses;

/** Result of a successful login. */
public record LoginResponseDTO(
        String token,
        String tokenType,
        long expiresIn,
        UserResponseDTO user
) {
}
