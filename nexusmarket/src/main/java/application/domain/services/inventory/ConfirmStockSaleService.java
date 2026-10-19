package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.StockAllocation;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Turns into a SALE the stock reserved by an order that has just been paid (the units leave the warehouse).
 */
public class ConfirmStockSaleService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RecordInventoryMovementService recordInventoryMovementService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ConfirmStockSaleService(InventoryRepositoryPort inventoryRepositoryPort,
            RecordInventoryMovementService recordInventoryMovementService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.recordInventoryMovementService = recordInventoryMovementService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public void execute(User actor, Order order) {
        for (OrderItem item : order.getItems()) {
            for (StockAllocation allocation : item.getAllocations()) {
                Inventory inventory = inventoryRepositoryPort.findById(allocation.getInventory())
                        .orElseThrow(() -> new EntityNotFoundException("Inventory", allocation.getInventory().getId()));
                inventory.confirmSale(allocation.getQuantity());
                inventoryRepositoryPort.update(inventory);
                recordInventoryMovementService.execute(actor, inventory, MovementType.SALE,
                        allocation.getQuantity(), "Order " + order.getId());
                registerOperationAndAuditService.execute(
                        Operation.of(OperationType.STOCK_SALE, actor, inventory, LocalDateTime.now(clock)),
                        AuditDetails.of("quantity", allocation.getQuantity(), "orderId", order.getId()));
            }
        }
    }
}
