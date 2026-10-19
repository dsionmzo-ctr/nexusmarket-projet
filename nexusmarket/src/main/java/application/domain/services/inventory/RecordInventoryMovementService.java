package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.valueobjects.MovementType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Appends an entry to the immutable inventory movement ledger (Ingreso, Reserva, Salida por venta, Ajuste,
 * Devolución).
 */
public class RecordInventoryMovementService {

    private final InventoryMovementRepositoryPort inventoryMovementRepositoryPort;
    private final Clock clock;

    public RecordInventoryMovementService(InventoryMovementRepositoryPort inventoryMovementRepositoryPort,
            Clock clock) {
        this.inventoryMovementRepositoryPort = inventoryMovementRepositoryPort;
        this.clock = clock;
    }

    public InventoryMovement execute(User actor, Inventory inventory, MovementType type, int quantity, String note) {
        InventoryMovement movement = InventoryMovement.of(inventory, type, quantity, actor, LocalDateTime.now(clock), note);
        return inventoryMovementRepositoryPort.save(movement);
    }
}
