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
import application.domain.valueobjects.WarehouseStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Activates or deactivates a warehouse. Only an ADMIN can do it.
 */
public class ChangeWarehouseStatusService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindWarehouseService findWarehouseService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ChangeWarehouseStatusService(AuthorizeOperationService authorizeOperationService,
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

    public Warehouse execute(User admin, Warehouse warehouse, WarehouseStatus newStatus) {
        authorizeOperationService.execute(admin, SystemRole.ADMIN);
        Warehouse stored = findWarehouseService.execute(warehouse);
        WarehouseStatus previous = stored.getStatus();
        stored.changeStatus(newStatus);
        warehouseRepositoryPort.update(stored);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.WAREHOUSE_STATUS_CHANGE, admin, stored, LocalDateTime.now(clock)),
                AuditDetails.of("previousStatus", previous.getCode(), "newStatus", newStatus.getCode()));
        return stored;
    }
}
