package application.domain.exceptions;

/**
 * Business rule violation of the refund aggregate.
 */
public class InvalidRefundException extends DomainException {

    public InvalidRefundException(String message) {
        super(message);
    }
}
