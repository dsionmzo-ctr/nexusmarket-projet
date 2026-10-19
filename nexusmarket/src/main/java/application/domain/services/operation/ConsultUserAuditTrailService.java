package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the audit records of the operations performed by a specific user (ADMIN and SUPERVISOR).
 */
public class ConsultUserAuditTrailService {

    private final AuthorizeOperationService authorizeOperationService;
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public ConsultUserAuditTrailService(AuthorizeOperationService authorizeOperationService,
            AuditLogRepositoryPort auditLogRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public List<AuditLog> execute(User requester, User performer) {
        authorizeOperationService.execute(requester, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return auditLogRepositoryPort.findByUser(performer);
    }
}
