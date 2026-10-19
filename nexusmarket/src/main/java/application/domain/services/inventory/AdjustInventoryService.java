package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A LOGISTIC_OPERATOR corrects the physical stock of an inventory record (ADJUSTMENT). The stock can never
 * be negative nor lower than the reserved quantity.
 */
public class AdjustInventoryService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindInventoryService findInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RecordInventoryMovementService recordInventoryMovementService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public AdjustInventoryService(AuthorizeOperationService authorizeOperationService,
            FindInventoryService findInventoryService,
            InventoryRepositoryPort inventoryRepositoryPort,
            RecordInventoryMovementService recordInventoryMovementService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeOperationService = authorizeOperationService;
        this.findInventoryService = findInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.recordInventoryMovementService = recordInventoryMovementService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Inventory execute(User user, InventoryMovement movement) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR);
        Inventory inventory = findInventoryService.execute(movement.getInventory());
        int previous = inventory.getOnHand();
        inventory.adjustTo(movement.getQuantity());
        inventoryRepositoryPort.update(inventory);
        recordInventoryMovementService.execute(user, inventory, MovementType.ADJUSTMENT,
                Math.abs(inventory.getOnHand() - previous), movement.getNote());
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.INVENTORY_ADJUSTMENT, user, inventory, LocalDateTime.now(clock)),
                AuditDetails.of("previousOnHand", previous, "newOnHand", inventory.getOnHand(), "note", movement.getNote()));
        return inventory;
    }
}
