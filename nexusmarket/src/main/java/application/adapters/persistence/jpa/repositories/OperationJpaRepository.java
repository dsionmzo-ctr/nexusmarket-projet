package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of OperationJpaEntity. Derived queries only; no business rules. */
public interface OperationJpaRepository extends JpaRepository<OperationJpaEntity, Long> {

    List<OperationJpaEntity> findByPerformedBy_IdOrderByExecutionDateDesc(Long userId);
    List<OperationJpaEntity> findByResourceTypeAndResourceIdOrderByExecutionDateDesc(String resourceType, Long resourceId);
    List<OperationJpaEntity> findByOperationTypeOrderByExecutionDateDesc(String operationType);
    List<OperationJpaEntity> findAllByOrderByExecutionDateDesc();
}
