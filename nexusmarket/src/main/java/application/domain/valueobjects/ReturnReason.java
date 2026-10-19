package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Reason given by the buyer for a return.
 */
public final class ReturnReason extends DomainCatalog {

    public static final ReturnReason DEFECTIVE = new ReturnReason(
            "DEFECTIVE", "Defective", "Product arrived defective.");
    public static final ReturnReason WRONG_ITEM = new ReturnReason(
            "WRONG_ITEM", "Wrong Item", "Product different from the one purchased.");
    public static final ReturnReason NOT_AS_DESCRIBED = new ReturnReason(
            "NOT_AS_DESCRIBED", "Not As Described", "Product does not match its description.");
    public static final ReturnReason CHANGED_MIND = new ReturnReason(
            "CHANGED_MIND", "Changed Mind", "Buyer changed their mind.");

    private static final List<ReturnReason> VALUES = List.of(DEFECTIVE, WRONG_ITEM, NOT_AS_DESCRIBED, CHANGED_MIND);

    private ReturnReason(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReturnReason> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static ReturnReason fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "ReturnReason");
    }
}
