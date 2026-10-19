package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Finds the inventory record of a variant in a warehouse, creating it (empty) on the first inflow.
 */
public class FindOrCreateInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public FindOrCreateInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public Inventory execute(Product product, ProductVariant variant, Warehouse warehouse) {
        Inventory probe = new Inventory();
        probe.setProduct(product);
        probe.setVariant(variant);
        probe.setWarehouse(warehouse);
        return inventoryRepositoryPort.findByVariantAndWarehouse(probe).orElseGet(() -> {
            probe.ensureValid();
            return inventoryRepositoryPort.save(probe);
        });
    }
}
