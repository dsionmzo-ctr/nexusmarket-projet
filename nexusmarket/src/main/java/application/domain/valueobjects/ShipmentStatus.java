package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Lifecycle of a shipment.
 */
public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus PREPARING = new ShipmentStatus(
            "PREPARING", "Preparing", "Order being packed.");
    public static final ShipmentStatus SHIPPED = new ShipmentStatus(
            "SHIPPED", "Shipped", "Shipment on its way.");
    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Delivered", "Shipment delivered to the buyer.");

    private static final List<ShipmentStatus> VALUES = List.of(PREPARING, SHIPPED, DELIVERED);

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ShipmentStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static ShipmentStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "ShipmentStatus");
    }
}
