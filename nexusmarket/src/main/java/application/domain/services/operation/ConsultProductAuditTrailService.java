package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceResource;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the audit records that affected a specific marketplace resource (ADMIN and SUPERVISOR).
 */
public class ConsultProductAuditTrailService {

    private final AuthorizeOperationService authorizeOperationService;
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public ConsultProductAuditTrailService(AuthorizeOperationService authorizeOperationService,
            AuditLogRepositoryPort auditLogRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public List<AuditLog> execute(User user, MarketplaceResource resource) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return auditLogRepositoryPort.findByProduct(resource);
    }
}
