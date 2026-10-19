package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Quality status of an inventory record.
 */
public final class InventoryStatus extends DomainCatalog {

    public static final InventoryStatus AVAILABLE = new InventoryStatus(
            "AVAILABLE", "Available", "Stock can be reserved.");
    public static final InventoryStatus DAMAGED = new InventoryStatus(
            "DAMAGED", "Damaged", "Stock cannot be reserved.");

    private static final List<InventoryStatus> VALUES = List.of(AVAILABLE, DAMAGED);

    private InventoryStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<InventoryStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static InventoryStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "InventoryStatus");
    }
}
