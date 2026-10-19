package application.domain.exceptions;

/**
 * Raised when a status change is not allowed by the transition matrix of an entity.
 */
public class InvalidStatusTransitionException extends DomainException {

    public InvalidStatusTransitionException(String entity, Object from, Object to) {
        super(entity + " cannot change from status " + from + " to " + to + ".");
    }
}
