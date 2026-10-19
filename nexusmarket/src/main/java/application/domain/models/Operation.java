package application.domain.models;

import application.domain.exceptions.InvalidOperationException;
import application.domain.valueobjects.OperationType;
import java.time.LocalDateTime;

/**
 * Significant business action executed by a user over a marketplace resource. It represents an
 * event that occurred and is distinct from the current status of the affected resource.
 * Every significant Operation is also recorded in the {@link AuditLog}.
 */
public class Operation {

    private Long operationId;
    private OperationType operationType;
    private LocalDateTime executionDate;
    private User performedBy;
    private MarketplaceResource affectedResource;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public MarketplaceResource getAffectedResource() {
        return affectedResource;
    }

    public void setAffectedResource(MarketplaceResource affectedResource) {
        this.affectedResource = affectedResource;
    }


    public static Operation of(OperationType type, User performedBy, MarketplaceResource resource, LocalDateTime now) {
        Operation operation = new Operation();
        operation.operationType = type;
        operation.executionDate = now;
        operation.performedBy = performedBy;
        operation.affectedResource = resource;
        return operation;
    }

    public void ensureValid() {
        if (operationType == null) {
            throw new InvalidOperationException("Operation type must be provided.");
        }
        if (executionDate == null) {
            throw new InvalidOperationException("Operation execution date must be provided.");
        }
        if (performedBy == null) {
            throw new InvalidOperationException("Operation performing user must be provided.");
        }
        if (affectedResource == null) {
            throw new InvalidOperationException("Operation affected resource must be provided.");
        }
    }

}
