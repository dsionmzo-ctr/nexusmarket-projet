package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the audit trail (ADMIN and SUPERVISOR). The filter optionally carries the performing user
 * and/or the operation type; when both are present both conditions apply.
 */
public class ConsultAuditLogsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public ConsultAuditLogsService(AuthorizeOperationService authorizeOperationService,
            AuditLogRepositoryPort auditLogRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public List<AuditLog> execute(User user, AuditLog filter) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        if (filter == null) {
            return auditLogRepositoryPort.findAll();
        }
        if (filter.getPerformedBy() != null) {
            List<AuditLog> byUser = auditLogRepositoryPort.findByUser(filter.getPerformedBy());
            if (filter.getOperationType() == null) {
                return byUser;
            }
            return byUser.stream().filter(a -> filter.getOperationType().equals(a.getOperationType())).toList();
        }
        if (filter.getOperationType() != null) {
            return auditLogRepositoryPort.findByOperationType(filter);
        }
        return auditLogRepositoryPort.findAll();
    }
}
