package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.AuditLogPageResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;

import java.util.LinkedHashMap;
import java.util.List;

/** AuditLog Domain Model <-> audit DTOs and query filter. */
public final class AuditLogRestMapper {

    private AuditLogRestMapper() {
    }

    /** Filter by performer and/or operation type; both null means "all entries". */
    public static AuditLog toFilter(Long userId, String operationTypeCode) {
        AuditLog filter = new AuditLog();
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            filter.setPerformedBy(user);
        }
        if (operationTypeCode != null && !operationTypeCode.isBlank()) {
            filter.setOperationType(OperationType.fromCode(operationTypeCode));
        }
        return filter;
    }

    public static AuditLogResponseDTO toResponse(AuditLog auditLog) {
        return new AuditLogResponseDTO(auditLog.getAuditId(), auditLog.getOperationType().getCode(),
                auditLog.getOperationDate(),
                auditLog.getPerformedBy() == null ? null : auditLog.getPerformedBy().getId(),
                auditLog.getPerformedBy() == null ? null : auditLog.getPerformedBy().getEmail(),
                auditLog.getUserRole().getCode(), auditLog.getAffectedResource().getResourceType(),
                auditLog.getAffectedResource().getId(), new LinkedHashMap<>(auditLog.getDetails()),
                auditLog.getSeverity().name());
    }

    /** In-memory paging of an already ordered list. page >= 0, size >= 1. */
    public static AuditLogPageResponseDTO toPage(List<AuditLog> logs, int page, int size) {
        long total = logs.size();
        int totalPages = (int) Math.ceil(total / (double) size);
        int from = (int) Math.min((long) page * size, total);
        int to = (int) Math.min((long) from + size, total);
        List<AuditLogResponseDTO> content = logs.subList(from, to).stream().map(AuditLogRestMapper::toResponse).toList();
        return new AuditLogPageResponseDTO(content, page, size, total, totalPages);
    }
}
