package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Lifecycle of an order.
 */
public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pending Payment", "Waiting for financial confirmation.");
    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Paid", "Payment confirmed; preparation starts.");
    public static final OrderStatus SHIPPED = new OrderStatus(
            "SHIPPED", "Shipped", "Order physically left the warehouse.");
    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Delivered", "Delivery completed; the order is finalized.");
    public static final OrderStatus CANCELLED = new OrderStatus(
            "CANCELLED", "Cancelled", "Order cancelled before payment; the order is finalized.");

    private static final List<OrderStatus> VALUES = List.of(PENDING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELLED);

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OrderStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static OrderStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "OrderStatus");
    }
}
