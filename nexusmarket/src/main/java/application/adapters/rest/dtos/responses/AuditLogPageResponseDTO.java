package application.adapters.rest.dtos.responses;

import java.util.List;

/** Page of audit log entries. */
public record AuditLogPageResponseDTO(
        List<AuditLogResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
