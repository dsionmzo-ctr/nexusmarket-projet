package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Visibility lifecycle of a catalog product.
 */
public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Published", "Product visible in the public catalog.");
    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspended", "Product temporarily not visible (also the initial status).");
    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Discontinued", "Product permanently withdrawn (terminal).");

    private static final List<ProductStatus> VALUES = List.of(PUBLISHED, SUSPENDED, DISCONTINUED);

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static ProductStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "ProductStatus");
    }
}
