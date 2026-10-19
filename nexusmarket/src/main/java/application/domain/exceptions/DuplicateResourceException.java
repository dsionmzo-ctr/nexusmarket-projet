package application.domain.exceptions;

/**
 * Raised when a unique value (e-mail, national id, SKU...) already exists.
 */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
