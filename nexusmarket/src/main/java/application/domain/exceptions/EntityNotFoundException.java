package application.domain.exceptions;

/**
 * Raised when a referenced entity does not exist or is not visible to the user.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entity, Object identifier) {
        super(entity + " not found" + (identifier == null ? "." : " with identifier '" + identifier + "'."));
    }
}
