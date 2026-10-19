package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;

/**
 * Relational representation of the stock reserved for an order line from one inventory record.
 */
@Entity
@Table(name = "order_item_allocations")
public class StockAllocationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemJpaEntity orderItem;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryJpaEntity inventory;
    @Column(nullable = false)
    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItemJpaEntity getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemJpaEntity orderItem) {
        this.orderItem = orderItem;
    }

    public InventoryJpaEntity getInventory() {
        return inventory;
    }

    public void setInventory(InventoryJpaEntity inventory) {
        this.inventory = inventory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
