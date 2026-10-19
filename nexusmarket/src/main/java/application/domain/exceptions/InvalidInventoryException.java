package application.domain.exceptions;

/**
 * Business rule violation of the inventory aggregate.
 */
public class InvalidInventoryException extends DomainException {

    public InvalidInventoryException(String message) {
        super(message);
    }
}
