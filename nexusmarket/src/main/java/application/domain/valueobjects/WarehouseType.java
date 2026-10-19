package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Ownership classification of a warehouse.
 */
public final class WarehouseType extends DomainCatalog {

    public static final WarehouseType MARKETPLACE = new WarehouseType(
            "MARKETPLACE", "Marketplace", "Warehouse operated by the marketplace.");
    public static final WarehouseType SELLER = new WarehouseType(
            "SELLER", "Seller", "Warehouse owned by one seller.");

    private static final List<WarehouseType> VALUES = List.of(MARKETPLACE, SELLER);

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<WarehouseType> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static WarehouseType fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "WarehouseType");
    }
}
