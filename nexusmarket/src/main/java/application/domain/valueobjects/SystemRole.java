package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Responsibilities and permissions of a person within the marketplace (RG-02: one role per user).
 */
public final class SystemRole extends DomainCatalog {

    public static final SystemRole BUYER = new SystemRole(
            "BUYER", "Buyer", "Person who purchases published products.");
    public static final SystemRole SELLER = new SystemRole(
            "SELLER", "Seller", "Third party responsible for registering and managing its own products.");
    public static final SystemRole LOGISTIC_OPERATOR = new SystemRole(
            "LOGISTIC_OPERATOR", "Logistic Operator", "Operator of the physical warehouses and dispatches.");
    public static final SystemRole ADMIN = new SystemRole(
            "ADMIN", "Admin", "Administrator of sellers, warehouses, staff users and refunds.");
    public static final SystemRole SUPERVISOR = new SystemRole(
            "SUPERVISOR", "Supervisor", "Read-only profile for operational follow-up.");

    private static final List<SystemRole> VALUES = List.of(BUYER, SELLER, LOGISTIC_OPERATOR, ADMIN, SUPERVISOR);

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<SystemRole> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static SystemRole fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "SystemRole");
    }
}
