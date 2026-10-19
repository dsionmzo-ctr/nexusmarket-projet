package application.domain.valueobjects;

import application.domain.exceptions.InvalidCatalogCodeException;

import java.util.List;
import java.util.Objects;

/**
 * Abstract business catalog (Value Object). Every controlled business value has a unique
 * {@code code}, a human readable {@code name} and a business {@code description}.
 *
 * <p>Immutable; equality is determined by the code (value), not by identity. Concrete
 * catalogs expose a validated {@code fromCode(String)} factory that rejects unknown codes.
 */
public abstract class DomainCatalog {

    private final String code;
    private final String name;
    private final String description;

    protected DomainCatalog(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /** Shared validated lookup used by the {@code fromCode} factory of every concrete catalog. */
    protected static <T extends DomainCatalog> T lookup(List<T> values, String code, String catalogName) {
        if (code != null) {
            for (T value : values) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
        }
        throw new InvalidCatalogCodeException(catalogName, code);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return code.equals(((DomainCatalog) other).code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), code);
    }

    @Override
    public String toString() {
        return code;
    }
}
