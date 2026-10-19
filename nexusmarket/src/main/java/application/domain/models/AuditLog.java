package application.domain.models;

import application.domain.enums.AuditSeverity;
import application.domain.exceptions.InvalidAuditLogException;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable, append-only audit record of a significant business operation (persisted in the NoSQL
 * audit database). It preserves what happened, when, who did it, with which role, over which
 * resource and operation-specific details. It is never used to compute stock or balances.
 */
public class AuditLog {

    private String auditId;
    private OperationType operationType;
    private LocalDateTime operationDate;
    private User performedBy;
    private SystemRole userRole;
    private MarketplaceResource affectedResource;
    private Map<String, Object> details = new LinkedHashMap<>();
    private AuditSeverity severity = AuditSeverity.INFORMATION;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public SystemRole getUserRole() {
        return userRole;
    }

    public void setUserRole(SystemRole userRole) {
        this.userRole = userRole;
    }

    public MarketplaceResource getAffectedResource() {
        return affectedResource;
    }

    public void setAffectedResource(MarketplaceResource affectedResource) {
        this.affectedResource = affectedResource;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public AuditSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AuditSeverity severity) {
        this.severity = severity;
    }


    public static AuditLog record(Operation operation, Map<String, Object> details) {
        AuditLog log = new AuditLog();
        log.operationType = operation.getOperationType();
        log.operationDate = operation.getExecutionDate();
        log.performedBy = operation.getPerformedBy();
        log.userRole = operation.getPerformedBy() == null ? null : operation.getPerformedBy().getRole();
        log.affectedResource = operation.getAffectedResource();
        log.details = details == null ? new LinkedHashMap<>() : new LinkedHashMap<>(details);
        return log;
    }

    public void ensureValid() {
        if (operationType == null) {
            throw new InvalidAuditLogException("Audit log operation type must be provided.");
        }
        if (operationDate == null) {
            throw new InvalidAuditLogException("Audit log operation date must be provided.");
        }
        if (performedBy == null || userRole == null) {
            throw new InvalidAuditLogException("Audit log performing user and role must be provided.");
        }
        if (affectedResource == null) {
            throw new InvalidAuditLogException("Audit log affected resource must be provided.");
        }
    }

}
