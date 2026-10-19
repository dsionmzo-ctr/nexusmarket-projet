package application.domain.models;

import application.domain.exceptions.ImmutableOrderException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Formal commercial commitment created when a cart is confirmed.
 *
 * Lifecycle: PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED, or PENDING_PAYMENT -> CANCELLED.
 * Physical orders must be shipped before being delivered; orders made only of digital products are
 * delivered right after payment. A finalized order (DELIVERED or CANCELLED) can never be modified.
 */
public class Order extends MarketplaceResource {

    private Buyer buyer;
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;
    private List<OrderItem> items = new ArrayList<>();
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime deliveredAt;

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }


    @Override
    public String getResourceType() {
        return "ORDER";
    }

    /** Creates an order in PENDING_PAYMENT from already validated and priced lines. */
    public static Order place(Buyer buyer, List<OrderItem> items, Address shippingAddress, LocalDateTime now) {
        if (buyer == null) {
            throw new InvalidOrderException("Order requires a buyer.");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("An order requires at least one item.");
        }
        items.forEach(OrderItem::ensureValid);
        Money first = items.get(0).getUnitPrice();
        boolean sameCurrency = items.stream().allMatch(i -> i.getUnitPrice().hasSameCurrency(first));
        if (!sameCurrency) {
            throw new InvalidOrderException("All order items must use the same currency.");
        }
        Order order = new Order();
        order.buyer = buyer;
        order.items = new ArrayList<>(items);
        order.shippingAddress = shippingAddress;
        order.createdAt = now;
        order.status = OrderStatus.PENDING_PAYMENT;
        if (order.hasPhysicalItems() && shippingAddress == null) {
            throw new InvalidOrderException("A shipping address is required for physical products.");
        }
        return order;
    }

    public Money total() {
        Money sum = Money.zero(items.get(0).getUnitPrice().currency());
        for (OrderItem item : items) {
            sum = sum.add(item.lineTotal());
        }
        return sum;
    }

    public boolean hasPhysicalItems() {
        return items.stream().anyMatch(OrderItem::requiresShipping);
    }

    public boolean isFinalized() {
        return OrderStatus.DELIVERED.equals(status) || OrderStatus.CANCELLED.equals(status);
    }

    public boolean belongsTo(Buyer candidate) {
        return buyer != null && candidate != null && buyer.getId() != null && buyer.getId().equals(candidate.getId());
    }

    public boolean containsSeller(Seller candidate) {
        return candidate != null && items.stream()
                .anyMatch(i -> i.getSeller().getId() != null && i.getSeller().getId().equals(candidate.getId()));
    }

    /** PENDING_PAYMENT -> PAID. */
    public void markPaid(PaymentMethod method, LocalDateTime now) {
        requireModifiable();
        if (method == null) {
            throw new InvalidOrderException("Payment method must be provided.");
        }
        requireStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        this.status = OrderStatus.PAID;
        this.paymentMethod = method;
        this.paidAt = now;
    }

    /** PAID -> SHIPPED (only orders with physical items). */
    public void markShipped() {
        requireModifiable();
        requireStatus(OrderStatus.PAID, OrderStatus.SHIPPED);
        if (!hasPhysicalItems()) {
            throw new InvalidStatusTransitionException("Order", status, OrderStatus.SHIPPED);
        }
        this.status = OrderStatus.SHIPPED;
    }

    /** SHIPPED -> DELIVERED, or PAID -> DELIVERED when the order has only digital items. */
    public void markDelivered(LocalDateTime now) {
        requireModifiable();
        boolean digitalOnlyPaid = OrderStatus.PAID.equals(status) && !hasPhysicalItems();
        if (!OrderStatus.SHIPPED.equals(status) && !digitalOnlyPaid) {
            throw new InvalidStatusTransitionException("Order", status, OrderStatus.DELIVERED);
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = now;
    }

    /** PENDING_PAYMENT -> CANCELLED. */
    public void cancel() {
        requireModifiable();
        requireStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
    }

    /** Read view for a seller: same order, but only the lines of that seller. */
    public Order forSeller(Seller candidate) {
        Order view = new Order();
        view.setId(getId());
        view.buyer = buyer;
        view.status = status;
        view.shippingAddress = shippingAddress;
        view.paymentMethod = paymentMethod;
        view.createdAt = createdAt;
        view.paidAt = paidAt;
        view.deliveredAt = deliveredAt;
        view.items = new ArrayList<>(items.stream()
                .filter(i -> i.getSeller().getId() != null && i.getSeller().getId().equals(candidate.getId()))
                .toList());
        return view;
    }

    private void requireModifiable() {
        if (isFinalized()) {
            throw new ImmutableOrderException(getId());
        }
    }

    private void requireStatus(OrderStatus expected, OrderStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Order", status, target);
        }
    }

}
