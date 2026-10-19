package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/** Spring Data MongoDB repository of the audit log collection. Derived queries only. */
public interface AuditLogMongoRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByPerformedByIdOrderByOperationDateDesc(Long userId);

    List<AuditLogDocument> findByResourceTypeAndResourceIdOrderByOperationDateDesc(String resourceType, Long resourceId);

    List<AuditLogDocument> findByOperationTypeOrderByOperationDateDesc(String operationType);

    List<AuditLogDocument> findAllByOrderByOperationDateDesc();
}
