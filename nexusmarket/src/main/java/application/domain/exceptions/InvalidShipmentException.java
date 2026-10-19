package application.domain.exceptions;

/**
 * Business rule violation of the shipment aggregate.
 */
public class InvalidShipmentException extends DomainException {

    public InvalidShipmentException(String message) {
        super(message);
    }
}
