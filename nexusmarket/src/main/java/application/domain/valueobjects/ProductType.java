package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Kind of product offered by the marketplace.
 */
public final class ProductType extends DomainCatalog {

    public static final ProductType PHYSICAL = new ProductType(
            "PHYSICAL", "Physical", "Good that requires inventory and shipping.");
    public static final ProductType DIGITAL = new ProductType(
            "DIGITAL", "Digital", "Good delivered immediately after payment.");

    private static final List<ProductType> VALUES = List.of(PHYSICAL, DIGITAL);

    private ProductType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductType> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static ProductType fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "ProductType");
    }
}
