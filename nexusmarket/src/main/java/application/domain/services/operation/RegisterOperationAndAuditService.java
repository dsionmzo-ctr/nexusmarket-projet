package application.domain.services.operation;

import application.domain.models.AuditLog;
import application.domain.models.Operation;
import java.util.Map;

/**
 * Single traceability registration point of the business services: persists the Operation and builds
 * and persists the corresponding AuditLog (operation type, date, user, role, resource and details).
 * The persistence adapters coordinate consistency with the business change (shared transaction).
 */
public class RegisterOperationAndAuditService {

    private final RegisterOperationService registerOperationService;
    private final RegisterAuditLogService registerAuditLogService;

    public RegisterOperationAndAuditService(RegisterOperationService registerOperationService,
            RegisterAuditLogService registerAuditLogService) {
        this.registerOperationService = registerOperationService;
        this.registerAuditLogService = registerAuditLogService;
    }

    public Operation execute(Operation operation, Map<String, Object> auditDetails) {
        Operation persisted = registerOperationService.execute(operation);
        registerAuditLogService.execute(AuditLog.record(operation, auditDetails));
        return persisted;
    }
}
