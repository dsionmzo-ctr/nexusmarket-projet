package application.domain.models;

import application.domain.valueobjects.MovementType;
import java.time.LocalDateTime;

/**
 * Immutable ledger entry of a change over an inventory record (Ingreso, Reserva, Salida por venta,
 * Ajuste, Devolución).
 */
public class InventoryMovement {

    private Long id;
    private Inventory inventory;
    private MovementType type;
    private int quantity;
    private int onHandAfter;
    private User performedBy;
    private LocalDateTime occurredAt;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOnHandAfter() {
        return onHandAfter;
    }

    public void setOnHandAfter(int onHandAfter) {
        this.onHandAfter = onHandAfter;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public static InventoryMovement of(Inventory inventory, MovementType type, int quantity, User performedBy,
                                       LocalDateTime occurredAt, String note) {
        InventoryMovement movement = new InventoryMovement();
        movement.inventory = inventory;
        movement.type = type;
        movement.quantity = quantity;
        movement.onHandAfter = inventory.getOnHand();
        movement.performedBy = performedBy;
        movement.occurredAt = occurredAt;
        movement.note = note;
        return movement;
    }

}
