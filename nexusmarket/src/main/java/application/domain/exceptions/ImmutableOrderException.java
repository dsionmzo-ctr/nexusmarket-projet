package application.domain.exceptions;

/**
 * Raised when someone tries to change a finalized order (validation: finalized orders are immutable).
 */
public class ImmutableOrderException extends DomainException {

    public ImmutableOrderException(Long orderId) {
        super("Order " + orderId + " is finalized and cannot be modified.");
    }
}
