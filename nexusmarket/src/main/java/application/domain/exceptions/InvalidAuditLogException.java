package application.domain.exceptions;

/**
 * Raised when an {@code AuditLog} record is incomplete or invalid.
 */
public class InvalidAuditLogException extends DomainException {
    public InvalidAuditLogException(String message) {
        super(message);
    }
}
