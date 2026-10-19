package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;
import java.util.Map;

/** Audit log entry. */
public record AuditLogResponseDTO(
        String auditId,
        String operationType,
        LocalDateTime operationDate,
        Long performedById,
        String performedByEmail,
        String userRole,
        String resourceType,
        Long resourceId,
        Map<String, Object> details,
        String severity
) {
}
