package application.domain.exceptions;

/**
 * Raised when a catalog code is not supported (validated {@code fromCode} factories).
 */
public class InvalidCatalogCodeException extends DomainException {
    public InvalidCatalogCodeException(String catalog, String code) {
        super("Unsupported " + catalog + " code: " + code);
    }
}
