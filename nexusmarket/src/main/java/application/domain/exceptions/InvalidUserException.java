package application.domain.exceptions;

/**
 * Business rule violation of the user aggregate.
 */
public class InvalidUserException extends DomainException {

    public InvalidUserException(String message) {
        super(message);
    }
}
