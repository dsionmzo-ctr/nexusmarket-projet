package application.domain.exceptions;

/**
 * Base class of every business-rule violation raised by the domain.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
