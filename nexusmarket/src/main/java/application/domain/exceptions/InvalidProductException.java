package application.domain.exceptions;

/**
 * Business rule violation of the product aggregate.
 */
public class InvalidProductException extends DomainException {

    public InvalidProductException(String message) {
        super(message);
    }
}
