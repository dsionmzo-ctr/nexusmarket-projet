package application.domain.services.operation;

import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.services.authorization.ValidateUserAuthorizationStatusService;
import java.util.List;

/**
 * Consults the operations performed by the authenticated user (any role: "my operations").
 */
public class ConsultUserOperationsService {

    private final ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService;
    private final OperationRepositoryPort operationRepositoryPort;

    public ConsultUserOperationsService(ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService,
            OperationRepositoryPort operationRepositoryPort) {
        this.validateUserAuthorizationStatusService = validateUserAuthorizationStatusService;
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public List<Operation> execute(User user) {
        validateUserAuthorizationStatusService.execute(user);
        return operationRepositoryPort.findByUser(user);
    }
}
