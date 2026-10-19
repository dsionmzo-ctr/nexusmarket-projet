package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.domain.models.Operation;
import application.domain.models.ResourceReference;
import application.domain.valueobjects.OperationType;

/** Operation Domain Model <-> OperationJpaEntity (the affected resource is stored as type + id). */
public final class OperationJpaMapper {

    private OperationJpaMapper() {
    }

    public static OperationJpaEntity toEntity(Operation domain) {
        OperationJpaEntity entity = new OperationJpaEntity();
        entity.setId(domain.getOperationId());
        entity.setOperationType(domain.getOperationType().getCode());
        entity.setExecutionDate(domain.getExecutionDate());
        entity.setPerformedBy(UserJpaMapper.ref(domain.getPerformedBy()));
        entity.setResourceType(domain.getAffectedResource().getResourceType());
        entity.setResourceId(domain.getAffectedResource().getId());
        return entity;
    }

    public static Operation toDomain(OperationJpaEntity entity) {
        Operation domain = new Operation();
        domain.setOperationId(entity.getId());
        domain.setOperationType(OperationType.fromCode(entity.getOperationType()));
        domain.setExecutionDate(entity.getExecutionDate());
        domain.setPerformedBy(UserJpaMapper.toShallowDomain(entity.getPerformedBy()));
        domain.setAffectedResource(new ResourceReference(entity.getResourceType(), entity.getResourceId()));
        return domain;
    }
}
