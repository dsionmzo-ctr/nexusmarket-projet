package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Relational representation of a shipment.
 */
@Entity
@Table(name = "shipments")
public class ShipmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;
    @Column(nullable = false, unique = true, length = 40)
    private String trackingNumber;
    @Embedded
    private AddressEmbeddable destination;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "handled_by_id", nullable = true)
    private UserJpaEntity handledBy;

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

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public AddressEmbeddable getDestination() {
        return destination;
    }

    public void setDestination(AddressEmbeddable destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public UserJpaEntity getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(UserJpaEntity handledBy) {
        this.handledBy = handledBy;
    }

}
