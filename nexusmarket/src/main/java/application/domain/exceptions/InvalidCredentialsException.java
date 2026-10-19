package application.domain.exceptions;

/**
 * Raised when the e-mail/password pair is wrong (same message for both cases).
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid credentials.");
    }
}
