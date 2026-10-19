package application.domain.services.inventory;

import application.domain.exceptions.InsufficientStockException;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.ProductVariant;
import application.domain.models.StockAllocation;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reserves the units of a variant taking them from the warehouses that can serve them (active warehouse,
 * AVAILABLE and non-damaged stock, largest availability first). Nothing is reserved when the total
 * available stock is not enough: no inexistent or damaged inventory can be reserved.
 */
public class ReserveStockService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RecordInventoryMovementService recordInventoryMovementService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public ReserveStockService(InventoryRepositoryPort inventoryRepositoryPort,
            RecordInventoryMovementService recordInventoryMovementService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.recordInventoryMovementService = recordInventoryMovementService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public List<StockAllocation> execute(User actor, ProductVariant variant, int quantity) {
        List<Inventory> candidates = inventoryRepositoryPort.findByVariant(variant).stream()
                .filter(i -> InventoryStatus.AVAILABLE.equals(i.getStatus()) && i.available() > 0)
                .filter(i -> i.getWarehouse().isActive())
                .sorted(Comparator.comparingInt(Inventory::available).reversed())
                .toList();
        int totalAvailable = candidates.stream().mapToInt(Inventory::available).sum();
        if (totalAvailable < quantity) {
            throw new InsufficientStockException("Not enough stock for variant " + variant.getId()
                    + ": requested " + quantity + ", available " + totalAvailable + ".");
        }
        List<StockAllocation> allocations = new ArrayList<>();
        int remaining = quantity;
        for (Inventory inventory : candidates) {
            if (remaining == 0) {
                break;
            }
            int take = Math.min(remaining, inventory.available());
            inventory.reserve(take);
            inventoryRepositoryPort.update(inventory);
            recordInventoryMovementService.execute(actor, inventory, MovementType.RESERVATION, take, null);
            registerOperationAndAuditService.execute(
                    Operation.of(OperationType.STOCK_RESERVATION, actor, inventory, LocalDateTime.now(clock)),
                    AuditDetails.of("quantity", take, "warehouseId", inventory.getWarehouse().getId(),
                            "variantId", variant.getId()));
            allocations.add(new StockAllocation(inventory, take));
            remaining -= take;
        }
        return allocations;
    }
}
