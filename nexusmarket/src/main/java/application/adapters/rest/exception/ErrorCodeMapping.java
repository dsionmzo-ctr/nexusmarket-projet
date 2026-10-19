package application.adapters.rest.exception;

import application.domain.exceptions.DamagedInventoryException;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.ImmutableOrderException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidAuditLogException;
import application.domain.exceptions.InvalidCartException;
import application.domain.exceptions.InvalidCatalogCodeException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidProductException;
import application.domain.exceptions.InvalidRefundException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.InvalidShipmentException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.InvalidWarehouseException;
import application.domain.exceptions.UnauthorizedOperationException;

/**
 * Centralized exception-to-HTTP mapping (Global exception handler specification §3). It is free of
 * Spring types so that it can be tested in isolation and reused by the exception handler.
 */
public final class ErrorCodeMapping {

    /** HTTP status and stable machine-readable code of a handled failure. */
    public record Mapping(int status, String code) {
    }

    private ErrorCodeMapping() {
    }

    public static Mapping forDomainException(DomainException ex) {
        if (ex instanceof InvalidCredentialsException) {
            return new Mapping(401, "INVALID_CREDENTIALS");
        }
        if (ex instanceof UnauthorizedOperationException) {
            return new Mapping(403, "FORBIDDEN");
        }
        if (ex instanceof EntityNotFoundException) {
            return new Mapping(404, "RESOURCE_NOT_FOUND");
        }
        if (ex instanceof DuplicateResourceException) {
            return new Mapping(409, "RESOURCE_ALREADY_EXISTS");
        }
        if (ex instanceof InvalidStatusTransitionException || ex instanceof ImmutableOrderException) {
            return new Mapping(409, "INVALID_STATE_TRANSITION");
        }
        if (ex instanceof InsufficientStockException) {
            return new Mapping(409, "INSUFFICIENT_STOCK");
        }
        if (ex instanceof DamagedInventoryException) {
            return new Mapping(409, "DAMAGED_INVENTORY");
        }
        if (ex instanceof InvalidCatalogCodeException) {
            return new Mapping(400, "INVALID_REQUEST");
        }
        if (ex instanceof InvalidOperationException || ex instanceof InvalidAuditLogException) {
            // The operation / audit records are built by the services, never by the client.
            return new Mapping(500, "INTERNAL_ERROR");
        }
        if (ex instanceof InvalidUserException) {
            return new Mapping(400, "INVALID_USER");
        }
        if (ex instanceof InvalidProductException) {
            return new Mapping(400, "INVALID_PRODUCT");
        }
        if (ex instanceof InvalidWarehouseException) {
            return new Mapping(400, "INVALID_WAREHOUSE");
        }
        if (ex instanceof InvalidCartException) {
            return new Mapping(400, "INVALID_CART");
        }
        if (ex instanceof InvalidOrderException) {
            return new Mapping(400, "INVALID_ORDER");
        }
        if (ex instanceof InvalidShipmentException) {
            return new Mapping(400, "INVALID_SHIPMENT");
        }
        if (ex instanceof InvalidReturnException) {
            return new Mapping(400, "INVALID_RETURN");
        }
        if (ex instanceof InvalidRefundException) {
            return new Mapping(400, "INVALID_REFUND");
        }
        if (ex instanceof InvalidInventoryException) {
            return new Mapping(400, "INVALID_INVENTORY");
        }
        return new Mapping(400, "INVALID_REQUEST");
    }

    /** Status of a Spring MVC framework failure that already carries its own HTTP status. */
    public static Mapping forHttpStatus(int status) {
        return switch (status) {
            case 404 -> new Mapping(404, "RESOURCE_NOT_FOUND");
            case 405 -> new Mapping(405, "METHOD_NOT_ALLOWED");
            case 406 -> new Mapping(406, "NOT_ACCEPTABLE");
            case 415 -> new Mapping(415, "UNSUPPORTED_MEDIA_TYPE");
            default -> status >= 400 && status < 500
                    ? new Mapping(status, "INVALID_REQUEST")
                    : new Mapping(500, "INTERNAL_ERROR");
        };
    }
}
