package application.domain.services.operation;

import application.domain.models.MarketplaceResource;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the operations that affected a specific marketplace resource (ADMIN and SUPERVISOR).
 */
public class ConsultProductOperationsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final OperationRepositoryPort operationRepositoryPort;

    public ConsultProductOperationsService(AuthorizeOperationService authorizeOperationService,
            OperationRepositoryPort operationRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public List<Operation> execute(User user, MarketplaceResource resource) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return operationRepositoryPort.findByProduct(resource);
    }
}
