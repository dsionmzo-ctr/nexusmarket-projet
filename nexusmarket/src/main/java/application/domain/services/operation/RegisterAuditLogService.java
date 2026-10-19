package application.domain.services.operation;

import application.domain.exceptions.InvalidAuditLogException;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;

/**
 * Registers an immutable AuditLog record through the AuditLogRepositoryPort. Audit records are
 * append-only: this service exposes no update operation.
 */
public class RegisterAuditLogService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public RegisterAuditLogService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public AuditLog execute(AuditLog auditLog) {
        if (auditLog == null) {
            throw new InvalidAuditLogException("Audit log must be provided.");
        }
        auditLog.ensureValid();
        return auditLogRepositoryPort.save(auditLog);
    }
}
