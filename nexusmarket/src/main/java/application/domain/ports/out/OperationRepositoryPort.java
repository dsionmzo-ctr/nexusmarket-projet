package application.domain.ports.out;

import application.domain.models.MarketplaceResource;
import application.domain.models.Operation;
import application.domain.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence of business Operation records (relational database).
 * Operation represents the business action executed by the system.
 */
public interface OperationRepositoryPort {

    Operation save(Operation operation);

    Optional<Operation> findById(Operation operation);

    List<Operation> findByUser(User user);

    List<Operation> findByProduct(MarketplaceResource resource);

    List<Operation> findByType(Operation operation);

    List<Operation> findAll();
}
