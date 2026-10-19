package application.domain.exceptions;

/**
 * Business rule violation of the warehouse aggregate.
 */
public class InvalidWarehouseException extends DomainException {

    public InvalidWarehouseException(String message) {
        super(message);
    }
}
