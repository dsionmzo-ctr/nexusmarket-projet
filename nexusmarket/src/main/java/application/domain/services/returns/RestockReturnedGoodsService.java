package application.domain.services.returns;

import application.domain.models.Inventory;
import application.domain.models.OrderItem;
import application.domain.models.ProductReturn;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.inventory.FindOrCreateInventoryService;
import application.domain.services.inventory.RecordInventoryMovementService;
import application.domain.valueobjects.MovementType;

/**
 * Makes the returned units re-enter the inventory of the receiving warehouse with a RETURN movement.
 */
public class RestockReturnedGoodsService {

    private final FindOrCreateInventoryService findOrCreateInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RecordInventoryMovementService recordInventoryMovementService;

    public RestockReturnedGoodsService(FindOrCreateInventoryService findOrCreateInventoryService,
            InventoryRepositoryPort inventoryRepositoryPort,
            RecordInventoryMovementService recordInventoryMovementService) {
        this.findOrCreateInventoryService = findOrCreateInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.recordInventoryMovementService = recordInventoryMovementService;
    }

    public Inventory execute(User actor, ProductReturn productReturn) {
        OrderItem item = productReturn.getOrderItem();
        Inventory inventory = findOrCreateInventoryService.execute(item.getProduct(), item.getVariant(),
                productReturn.getReceivedWarehouse());
        inventory.registerReturn(productReturn.getQuantity());
        inventoryRepositoryPort.update(inventory);
        recordInventoryMovementService.execute(actor, inventory, MovementType.RETURN, productReturn.getQuantity(),
                "Return " + productReturn.getId());
        return inventory;
    }
}
