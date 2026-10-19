package application.domain.services.operation;

import application.domain.exceptions.InvalidOperationException;
import application.domain.models.Operation;
import application.domain.ports.out.OperationRepositoryPort;

/**
 * Registers a business Operation through the OperationRepositoryPort after validating the information
 * available in the Operation Domain Model (traceability only; it never decides business validity).
 */
public class RegisterOperationService {

    private final OperationRepositoryPort operationRepositoryPort;

    public RegisterOperationService(OperationRepositoryPort operationRepositoryPort) {
        this.operationRepositoryPort = operationRepositoryPort;
    }

    public Operation execute(Operation operation) {
        if (operation == null) {
            throw new InvalidOperationException("Operation must be provided.");
        }
        operation.ensureValid();
        return operationRepositoryPort.save(operation);
    }
}
