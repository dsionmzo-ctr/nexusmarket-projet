package application.adapters.persistence.mongodb.adapters;

import application.adapters.persistence.mongodb.mappers.AuditLogMongoMapper;
import application.adapters.persistence.mongodb.repositories.AuditLogMongoRepository;
import application.domain.models.AuditLog;
import application.domain.models.MarketplaceResource;
import application.domain.models.User;
import application.domain.ports.out.AuditLogRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Persistence adapter of the audit log port (MongoDB). It is intentionally not transactional with
 * the relational database: the audit trail lives in a different store.
 */
@Repository
public class AuditLogMongoAdapter implements AuditLogRepositoryPort {

    private final AuditLogMongoRepository auditLogMongoRepository;

    public AuditLogMongoAdapter(AuditLogMongoRepository auditLogMongoRepository) {
        this.auditLogMongoRepository = auditLogMongoRepository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return AuditLogMongoMapper.toDomain(auditLogMongoRepository.save(AuditLogMongoMapper.toDocument(auditLog)));
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return auditLogMongoRepository.findByPerformedByIdOrderByOperationDateDesc(user.getId()).stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByProduct(MarketplaceResource resource) {
        return auditLogMongoRepository.findByResourceTypeAndResourceIdOrderByOperationDateDesc(
                resource.getResourceType(), resource.getId()).stream().map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByOperationType(AuditLog auditLog) {
        return auditLogMongoRepository.findByOperationTypeOrderByOperationDateDesc(
                auditLog.getOperationType().getCode()).stream().map(AuditLogMongoMapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogMongoRepository.findAllByOrderByOperationDateDesc().stream()
                .map(AuditLogMongoMapper::toDomain).toList();
    }
}
