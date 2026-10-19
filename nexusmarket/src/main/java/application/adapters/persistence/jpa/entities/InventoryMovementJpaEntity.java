package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Relational representation of an inventory movement ledger entry.
 */
@Entity
@Table(name = "inventory_movements")
public class InventoryMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryJpaEntity inventory;
    @Column(nullable = false, length = 30)
    private String type;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private int onHandAfter;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performed_by_id", nullable = true)
    private UserJpaEntity performedBy;
    @Column(nullable = false)
    private LocalDateTime occurredAt;
    @Column(length = 500)
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryJpaEntity getInventory() {
        return inventory;
    }

    public void setInventory(InventoryJpaEntity inventory) {
        this.inventory = inventory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public UserJpaEntity getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserJpaEntity performedBy) {
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

}
