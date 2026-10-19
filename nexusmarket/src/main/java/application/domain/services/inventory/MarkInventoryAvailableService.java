package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A LOGISTIC_OPERATOR marks a damaged inventory record as AVAILABLE again.
 */
public class MarkInventoryAvailableService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindInventoryService findInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public MarkInventoryAvailableService(AuthorizeOperationService authorizeOperationService,
            FindInventoryService findInventoryService,
            InventoryRepositoryPort inventoryRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findInventoryService = findInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Inventory execute(User user, InventoryMovement movement) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        Inventory inventory = findInventoryService.execute(movement.getInventory());
        inventory.markAvailable();
        inventoryRepositoryPort.update(inventory);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.INVENTORY_AVAILABILITY_RESTORATION, user, inventory, LocalDateTime.now(clock)),
                AuditDetails.of("note", movement.getNote()));
        return inventory;
    }
}
