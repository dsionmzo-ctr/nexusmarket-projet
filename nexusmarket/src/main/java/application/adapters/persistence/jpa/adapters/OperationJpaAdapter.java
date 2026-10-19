package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.OperationJpaMapper;
import application.adapters.persistence.jpa.repositories.OperationJpaRepository;
import application.domain.models.MarketplaceResource;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.OperationRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the operation repository port (relational operation history). */
@Repository
@Transactional
public class OperationJpaAdapter implements OperationRepositoryPort {

    private final OperationJpaRepository operationJpaRepository;

    public OperationJpaAdapter(OperationJpaRepository operationJpaRepository) {
        this.operationJpaRepository = operationJpaRepository;
    }

    @Override
    public Operation save(Operation operation) {
        return OperationJpaMapper.toDomain(operationJpaRepository.save(OperationJpaMapper.toEntity(operation)));
    }

    @Override
    public Optional<Operation> findById(Operation operation) {
        return operationJpaRepository.findById(operation.getOperationId()).map(OperationJpaMapper::toDomain);
    }

    @Override
    public List<Operation> findByUser(User user) {
        return operationJpaRepository.findByPerformedBy_IdOrderByExecutionDateDesc(user.getId()).stream()
                .map(OperationJpaMapper::toDomain).toList();
    }

    @Override
    public List<Operation> findByProduct(MarketplaceResource resource) {
        return operationJpaRepository.findByResourceTypeAndResourceIdOrderByExecutionDateDesc(
                resource.getResourceType(), resource.getId()).stream().map(OperationJpaMapper::toDomain).toList();
    }

    @Override
    public List<Operation> findByType(Operation operation) {
        return operationJpaRepository.findByOperationTypeOrderByExecutionDateDesc(
                operation.getOperationType().getCode()).stream().map(OperationJpaMapper::toDomain).toList();
    }

    @Override
    public List<Operation> findAll() {
        return operationJpaRepository.findAllByOrderByExecutionDateDesc().stream()
                .map(OperationJpaMapper::toDomain).toList();
    }
}
