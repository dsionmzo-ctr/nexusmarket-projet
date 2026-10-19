package application.domain.services.operation;

import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults every operation recorded by the system (ADMIN and SUPERVISOR).
 */
public class ConsultAllOperationsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final OperationRepositoryPort operationRepositoryPort;

    public ConsultAllOperationsService(AuthorizeOperationService authorizeOperationService,
            OperationRepositoryPort operationRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public List<Operation> execute(User user) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return operationRepositoryPort.findAll();
    }
}
