package application.domain.exceptions;

/**
 * Business rule violation of the order aggregate.
 */
public class InvalidOrderException extends DomainException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
