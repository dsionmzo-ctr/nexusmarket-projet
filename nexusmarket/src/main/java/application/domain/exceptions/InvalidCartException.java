package application.domain.exceptions;

/**
 * Business rule violation of the cart aggregate.
 */
public class InvalidCartException extends DomainException {

    public InvalidCartException(String message) {
        super(message);
    }
}
