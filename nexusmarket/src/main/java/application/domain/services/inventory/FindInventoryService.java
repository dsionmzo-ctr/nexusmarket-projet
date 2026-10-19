package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Loads the inventory of a variant in a warehouse (both taken from the supplied model) or fails with
 * EntityNotFoundException.
 */
public class FindInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public FindInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public Inventory execute(Inventory probe) {
        if (probe == null || probe.getVariant() == null || probe.getWarehouse() == null) {
            throw new InvalidInventoryException("The variant and the warehouse of the inventory must be provided.");
        }
        return inventoryRepositoryPort.findByVariantAndWarehouse(probe)
                .orElseThrow(() -> new EntityNotFoundException("Inventory", "variant "
                        + probe.getVariant().getId() + " / warehouse " + probe.getWarehouse().getId()));
    }
}
