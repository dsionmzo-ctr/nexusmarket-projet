package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Lifecycle of a refund.
 */
public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus REQUESTED = new RefundStatus(
            "REQUESTED", "Requested", "Refund opened together with the return.");
    public static final RefundStatus APPROVED = new RefundStatus(
            "APPROVED", "Approved", "Refund approved by the administrator.");
    public static final RefundStatus REJECTED = new RefundStatus(
            "REJECTED", "Rejected", "Refund rejected by the administrator.");

    private static final List<RefundStatus> VALUES = List.of(REQUESTED, APPROVED, REJECTED);

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<RefundStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static RefundStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "RefundStatus");
    }
}
