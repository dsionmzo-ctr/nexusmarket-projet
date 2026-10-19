package application.domain.exceptions;

/**
 * Business rule violation of the return aggregate.
 */
public class InvalidReturnException extends DomainException {

    public InvalidReturnException(String message) {
        super(message);
    }
}
