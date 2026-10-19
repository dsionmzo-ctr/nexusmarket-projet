package application.adapters.rest.exception;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.ImmutableOrderException;
import application.domain.exceptions.InvalidCartException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.InvalidProductException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Global exception handler specification: every domain failure maps to a stable HTTP status and code. */
class ErrorCodeMappingTest {

    private static void expect(DomainException exception, int status, String code) {
        ErrorCodeMapping.Mapping mapping = ErrorCodeMapping.forDomainException(exception);
        assertEquals(status, mapping.status());
        assertEquals(code, mapping.code());
    }

    @Test
    void domainFailuresMapToTheirHttpStatusAndCode() {
        expect(new InvalidCredentialsException(), 401, "INVALID_CREDENTIALS");
        expect(new UnauthorizedOperationException("no"), 403, "FORBIDDEN");
        expect(new EntityNotFoundException("Order", 9L), 404, "RESOURCE_NOT_FOUND");
        expect(new ImmutableOrderException(5L), 409, "INVALID_STATE_TRANSITION");
        expect(new application.domain.exceptions.InsufficientStockException("low"), 409, "INSUFFICIENT_STOCK");
        expect(new application.domain.exceptions.DamagedInventoryException("damaged"), 409, "DAMAGED_INVENTORY");
        expect(new application.domain.exceptions.DuplicateResourceException("dup"), 409, "RESOURCE_ALREADY_EXISTS");
        expect(new InvalidProductException("bad"), 400, "INVALID_PRODUCT");
        expect(new InvalidCartException("bad"), 400, "INVALID_CART");
        expect(new InvalidReturnException("bad"), 400, "INVALID_RETURN");
        expect(new DomainException("generic"), 400, "INVALID_REQUEST");
    }

    @Test
    void frameworkStatusesKeepTheirMeaning() {
        assertEquals("METHOD_NOT_ALLOWED", ErrorCodeMapping.forHttpStatus(405).code());
        assertEquals("NOT_ACCEPTABLE", ErrorCodeMapping.forHttpStatus(406).code());
        assertEquals("UNSUPPORTED_MEDIA_TYPE", ErrorCodeMapping.forHttpStatus(415).code());
        assertEquals("RESOURCE_NOT_FOUND", ErrorCodeMapping.forHttpStatus(404).code());
        assertEquals("INVALID_REQUEST", ErrorCodeMapping.forHttpStatus(422).code());
        assertEquals(500, ErrorCodeMapping.forHttpStatus(503).status());
    }
}
