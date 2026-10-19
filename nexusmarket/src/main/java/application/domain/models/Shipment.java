package application.domain.models;

import application.domain.exceptions.InvalidShipmentException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.ShipmentStatus;
import java.time.LocalDateTime;

/**
 * Logistic process of an order with physical items: PREPARING -> SHIPPED -> DELIVERED.
 */
public class Shipment extends MarketplaceResource {

    private Order order;
    private String trackingNumber;
    private Address destination;
    private ShipmentStatus status = ShipmentStatus.PREPARING;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private User handledBy;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
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

    public User getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(User handledBy) {
        this.handledBy = handledBy;
    }


    @Override
    public String getResourceType() {
        return "SHIPMENT";
    }

    public static Shipment prepareFor(Order order, String trackingNumber, LocalDateTime now) {
        if (order == null || order.getId() == null || !order.hasPhysicalItems()) {
            throw new InvalidShipmentException("A shipment requires a persisted order with physical items.");
        }
        Shipment shipment = new Shipment();
        shipment.order = order;
        shipment.trackingNumber = trackingNumber;
        shipment.destination = order.getShippingAddress();
        shipment.status = ShipmentStatus.PREPARING;
        shipment.createdAt = now;
        return shipment;
    }

    /** PREPARING -> SHIPPED. */
    public void dispatch(User operator, LocalDateTime now) {
        if (!ShipmentStatus.PREPARING.equals(status)) {
            throw new InvalidStatusTransitionException("Shipment", status, ShipmentStatus.SHIPPED);
        }
        this.status = ShipmentStatus.SHIPPED;
        this.shippedAt = now;
        this.handledBy = operator;
    }

    /** SHIPPED -> DELIVERED. */
    public void confirmDelivery(User operator, LocalDateTime now) {
        if (!ShipmentStatus.SHIPPED.equals(status)) {
            throw new InvalidStatusTransitionException("Shipment", status, ShipmentStatus.DELIVERED);
        }
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredAt = now;
        this.handledBy = operator;
    }

}
