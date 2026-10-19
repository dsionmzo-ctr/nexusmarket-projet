package application.adapters.rest.dtos.responses;

/** Standard error envelope returned by every failing endpoint. */
public record ErrorResponseDTO(
        String timestamp,
        int status,
        String code,
        String message,
        String path,
        String requestId,
        Object details
) {
}
