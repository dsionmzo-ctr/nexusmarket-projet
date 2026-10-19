package application.domain.exceptions;

/**
 * Raised when trying to reserve inventory marked as damaged.
 */
public class DamagedInventoryException extends DomainException {

    public DamagedInventoryException(String message) {
        super(message);
    }
}
