package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Lifecycle of a product return.
 */
public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Requested", "Return requested by the buyer.");
    public static final ReturnStatus RECEIVED = new ReturnStatus(
            "RECEIVED", "Received", "Returned goods received in a warehouse.");
    public static final ReturnStatus REJECTED = new ReturnStatus(
            "REJECTED", "Rejected", "Return rejected.");

    private static final List<ReturnStatus> VALUES = List.of(REQUESTED, RECEIVED, REJECTED);

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReturnStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static ReturnStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "ReturnStatus");
    }
}
