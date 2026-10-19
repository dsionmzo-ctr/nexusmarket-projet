package application.domain.services.warehouse;

import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Updates the name and the address of a warehouse. Only an ADMIN can do it.
 */
public class UpdateWarehouseService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindWarehouseService findWarehouseService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public UpdateWarehouseService(AuthorizeOperationService authorizeOperationService,
            FindWarehouseService findWarehouseService,
            WarehouseRepositoryPort warehouseRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findWarehouseService = findWarehouseService;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Warehouse execute(User admin, Warehouse changes) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        Warehouse stored = findWarehouseService.execute(changes);
        stored.update(changes.getName(), changes.getAddress());
        warehouseRepositoryPort.update(stored);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.WAREHOUSE_UPDATE, admin, stored, LocalDateTime.now(clock)),
                AuditDetails.of("name", stored.getName()));
        return stored;
    }
}
