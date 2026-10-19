package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Relational representation of a business operation (the affected resource is stored as type + identifier).
 */
@Entity
@Table(name = "operations")
public class OperationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String operationType;
    @Column(nullable = false)
    private LocalDateTime executionDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private UserJpaEntity performedBy;
    @Column(nullable = false, length = 30)
    private String resourceType;
    @Column(nullable = false)
    private Long resourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public UserJpaEntity getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserJpaEntity performedBy) {
        this.performedBy = performedBy;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

}
