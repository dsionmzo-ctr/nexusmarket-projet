package application.domain.ports.out;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import java.util.List;

/**
 * Output port: append-only ledger of inventory movements.
 */
public interface InventoryMovementRepositoryPort {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByInventory(Inventory inventory);
}
