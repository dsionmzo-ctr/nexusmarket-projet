package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Operation;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.AuthorizeInventoryOperationService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.warehouse.FindWarehouseService;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Registers stock entering a warehouse (INFLOW) for a physical product variant. A SELLER can only do it for
 * its own products in its own warehouses; a LOGISTIC_OPERATOR in any warehouse. The inventory record is
 * created on the first inflow.
 */
public class RegisterInventoryInflowService {

    private final AuthorizeInventoryOperationService authorizeInventoryOperationService;
    private final ProductRepositoryPort productRepositoryPort;
    private final FindWarehouseService findWarehouseService;
    private final FindOrCreateInventoryService findOrCreateInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final RecordInventoryMovementService recordInventoryMovementService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterInventoryInflowService(AuthorizeInventoryOperationService authorizeInventoryOperationService,
            ProductRepositoryPort productRepositoryPort,
            FindWarehouseService findWarehouseService,
            FindOrCreateInventoryService findOrCreateInventoryService,
            InventoryRepositoryPort inventoryRepositoryPort,
            RecordInventoryMovementService recordInventoryMovementService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.authorizeInventoryOperationService = authorizeInventoryOperationService;
        this.productRepositoryPort = productRepositoryPort;
        this.findWarehouseService = findWarehouseService;
        this.findOrCreateInventoryService = findOrCreateInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.recordInventoryMovementService = recordInventoryMovementService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Inventory execute(User user, InventoryMovement movement) {
        Inventory requested = movement.getInventory();
        if (requested == null || requested.getVariant() == null) {
            throw new InvalidInventoryException("The variant of the inventory must be provided.");
        }
        Product product = productRepositoryPort.findByVariant(requested.getVariant())
                .orElseThrow(() -> new EntityNotFoundException("Product variant", requested.getVariant().getId()));
        ProductVariant variant = product.findVariant(requested.getVariant().getId()).orElseThrow();
        Warehouse warehouse = findWarehouseService.execute(requested.getWarehouse());
        Inventory candidate = new Inventory();
        candidate.setProduct(product);
        candidate.setVariant(variant);
        candidate.setWarehouse(warehouse);
        authorizeInventoryOperationService.execute(user, candidate);
        if (!product.requiresInventory()) {
            throw new InvalidInventoryException("Digital products do not use inventory.");
        }
        if (!warehouse.isActive()) {
            throw new InvalidWarehouseException("The warehouse is not active.");
        }
        Inventory inventory = findOrCreateInventoryService.execute(product, variant, warehouse);
        inventory.receive(movement.getQuantity());
        inventoryRepositoryPort.update(inventory);
        recordInventoryMovementService.execute(user, inventory, MovementType.INFLOW, movement.getQuantity(),
                movement.getNote());
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.INVENTORY_INFLOW, user, inventory, LocalDateTime.now(clock)),
                AuditDetails.of("quantity", movement.getQuantity(), "onHand", inventory.getOnHand(),
                        "warehouseId", warehouse.getId(), "variantId", variant.getId()));
        return inventory;
    }
}
