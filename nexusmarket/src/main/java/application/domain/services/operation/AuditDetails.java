package application.domain.services.operation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Null-safe builder of the operation-specific details stored in an audit record.
 * Entries whose value is null are skipped.
 */
public final class AuditDetails {

    private AuditDetails() {
    }

    /** Builds a details map from alternating key/value arguments: {@code of("amount", 10, "currency", "COP")}. */
    public static Map<String, Object> of(Object... keyValues) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i + 1] != null) {
                details.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
            }
        }
        return details;
    }
}
