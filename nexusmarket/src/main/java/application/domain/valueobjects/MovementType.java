package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Kind of inventory movement.
 */
public final class MovementType extends DomainCatalog {

    public static final MovementType INFLOW = new MovementType(
            "INFLOW", "Inflow", "Stock entering a warehouse.");
    public static final MovementType RESERVATION = new MovementType(
            "RESERVATION", "Reservation", "Stock reserved by an order.");
    public static final MovementType RESERVATION_RELEASE = new MovementType(
            "RESERVATION_RELEASE", "Reservation Release", "Reserved stock given back (cancelled order).");
    public static final MovementType SALE = new MovementType(
            "SALE", "Sale", "Reserved stock leaving the warehouse after payment.");
    public static final MovementType ADJUSTMENT = new MovementType(
            "ADJUSTMENT", "Adjustment", "Manual correction of the physical stock.");
    public static final MovementType RETURN = new MovementType(
            "RETURN", "Return", "Stock coming back from a buyer return.");

    private static final List<MovementType> VALUES = List.of(INFLOW, RESERVATION, RESERVATION_RELEASE, SALE, ADJUSTMENT, RETURN);

    private MovementType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<MovementType> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static MovementType fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "MovementType");
    }
}
