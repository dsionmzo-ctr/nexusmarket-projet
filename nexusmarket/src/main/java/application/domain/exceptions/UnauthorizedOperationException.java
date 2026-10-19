package application.domain.exceptions;

/**
 * Raised when the authenticated user has no permission over the requested flow or resource.
 */
public class UnauthorizedOperationException extends DomainException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
