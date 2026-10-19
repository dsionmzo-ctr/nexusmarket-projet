package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the movement ledger of an inventory record (LOGISTIC_OPERATOR, ADMIN and SUPERVISOR).
 */
public class ConsultInventoryMovementsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindInventoryService findInventoryService;
    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;

    public ConsultInventoryMovementsService(AuthorizeOperationService authorizeOperationService,
            FindInventoryService findInventoryService,
            InventoryMovementRepositoryPort inventoryMovementRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.findInventoryService = findInventoryService;
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
    }

    public List<InventoryMovement> execute(User user, Inventory inventory) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return inventoryMovementRepositoryPort.findByInventory(findInventoryService.execute(inventory));
    }
}
