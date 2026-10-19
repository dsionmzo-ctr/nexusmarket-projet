package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Relational representation of a product return.
 */
@Entity
@Table(name = "product_returns")
public class ProductReturnJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemJpaEntity orderItem;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, length = 30)
    private String reason;
    @Column(length = 500)
    private String comment;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private LocalDateTime requestedAt;
    private LocalDateTime receivedAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "received_warehouse_id", nullable = true)
    private WarehouseJpaEntity receivedWarehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderJpaEntity getOrder() {
        return order;
    }

    public void setOrder(OrderJpaEntity order) {
        this.order = order;
    }

    public OrderItemJpaEntity getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemJpaEntity orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public WarehouseJpaEntity getReceivedWarehouse() {
        return receivedWarehouse;
    }

    public void setReceivedWarehouse(WarehouseJpaEntity receivedWarehouse) {
        this.receivedWarehouse = receivedWarehouse;
    }

}
