package application.domain.models;

import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;
import java.time.LocalDateTime;

/**
 * Return of a delivered product requested by a buyer. REQUESTED -> RECEIVED | REJECTED.
 */
public class ProductReturn extends MarketplaceResource {

    private Order order;
    private OrderItem orderItem;
    private int quantity;
    private ReturnReason reason;
    private String comment;
    private ReturnStatus status = ReturnStatus.REQUESTED;
    private LocalDateTime requestedAt;
    private LocalDateTime receivedAt;
    private Warehouse receivedWarehouse;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public void setReason(ReturnReason reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
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

    public Warehouse getReceivedWarehouse() {
        return receivedWarehouse;
    }

    public void setReceivedWarehouse(Warehouse receivedWarehouse) {
        this.receivedWarehouse = receivedWarehouse;
    }


    @Override
    public String getResourceType() {
        return "RETURN";
    }

    public static ProductReturn request(Order order, OrderItem item, int quantity, ReturnReason reason,
                                        String comment, LocalDateTime now) {
        if (reason == null) {
            throw new InvalidReturnException("Return reason must be provided.");
        }
        if (quantity <= 0 || quantity > item.getQuantity()) {
            throw new InvalidReturnException("Returned quantity must be between 1 and the purchased quantity.");
        }
        ProductReturn productReturn = new ProductReturn();
        productReturn.order = order;
        productReturn.orderItem = item;
        productReturn.quantity = quantity;
        productReturn.reason = reason;
        productReturn.comment = comment;
        productReturn.status = ReturnStatus.REQUESTED;
        productReturn.requestedAt = now;
        return productReturn;
    }

    /** REQUESTED -> RECEIVED once the goods reach a warehouse. */
    public void receive(Warehouse warehouse, LocalDateTime now) {
        if (!ReturnStatus.REQUESTED.equals(status)) {
            throw new InvalidStatusTransitionException("Return", status, ReturnStatus.RECEIVED);
        }
        if (warehouse == null) {
            throw new InvalidReturnException("The receiving warehouse must be provided.");
        }
        this.status = ReturnStatus.RECEIVED;
        this.receivedWarehouse = warehouse;
        this.receivedAt = now;
    }

    /** REQUESTED -> REJECTED. */
    public void reject() {
        if (!ReturnStatus.REQUESTED.equals(status)) {
            throw new InvalidStatusTransitionException("Return", status, ReturnStatus.REJECTED);
        }
        this.status = ReturnStatus.REJECTED;
    }

    public boolean isReceived() {
        return ReturnStatus.RECEIVED.equals(status);
    }

}
