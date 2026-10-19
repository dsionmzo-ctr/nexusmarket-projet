package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Operational status of a warehouse.
 */
public final class WarehouseStatus extends DomainCatalog {

    public static final WarehouseStatus ACTIVE = new WarehouseStatus(
            "ACTIVE", "Active", "Warehouse is operating.");
    public static final WarehouseStatus INACTIVE = new WarehouseStatus(
            "INACTIVE", "Inactive", "Warehouse is not operating.");

    private static final List<WarehouseStatus> VALUES = List.of(ACTIVE, INACTIVE);

    private WarehouseStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<WarehouseStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static WarehouseStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "WarehouseStatus");
    }
}
