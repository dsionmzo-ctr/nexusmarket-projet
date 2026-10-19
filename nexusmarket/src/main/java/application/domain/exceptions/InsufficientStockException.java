package application.domain.exceptions;

/**
 * Raised when there is not enough available stock; negative stock is never allowed.
 */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
