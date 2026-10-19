package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: Status of a user's access to the system.
 */
public final class UserStatus extends DomainCatalog {

    public static final UserStatus ACTIVE = new UserStatus(
            "ACTIVE", "Active", "User can access the system normally.");
    public static final UserStatus INACTIVE = new UserStatus(
            "INACTIVE", "Inactive", "User exists but cannot perform system operations.");
    public static final UserStatus BLOCKED = new UserStatus(
            "BLOCKED", "Blocked", "User access has been suspended.");

    private static final List<UserStatus> VALUES = List.of(ACTIVE, INACTIVE, BLOCKED);

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<UserStatus> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static UserStatus fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "UserStatus");
    }
}
