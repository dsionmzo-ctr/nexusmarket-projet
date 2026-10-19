package application.domain.exceptions;

/**
 * Raised when an {@code Operation} record is incomplete or invalid.
 */
public class InvalidOperationException extends DomainException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
