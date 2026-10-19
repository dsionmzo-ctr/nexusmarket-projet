package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Commercial status of a buyer (independent from the user access status).
 */
public final class BuyerStatus extends DomainCatalog {

    public static final BuyerStatus ENABLED = new BuyerStatus(
            "ENABLED", "Enabled", "Buyer can place purchases.");
    public static final BuyerStatus RESTRICTED = new BuyerStatus(
            "RESTRICTED", "Restricted", "Buyer is temporarily not allowed to purchase.");

    private static final List<BuyerStatus> VALUES = List.of(ENABLED, RESTRICTED);

    private BuyerStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<BuyerStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static BuyerStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "BuyerStatus");
    }
}
