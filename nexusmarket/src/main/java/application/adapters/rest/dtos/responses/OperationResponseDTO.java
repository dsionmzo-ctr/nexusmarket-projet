package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

/** Operation executed in the marketplace. */
public record OperationResponseDTO(
        Long id,
        String operationType,
        LocalDateTime executionDate,
        Long performedById,
        String resourceType,
        Long resourceId
) {
}
