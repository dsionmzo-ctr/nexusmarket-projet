package application.domain.valueobjects;

import java.util.List;

/**
 * Business catalog: monetary currency supported by the marketplace. It adds the ISO 4217
 * code and the display symbol to the common catalog attributes.
 */
public final class Currency extends DomainCatalog {

    public static final Currency COP = new Currency("COP", "Colombian Peso", "Colombian peso.", "COP", "$");
    public static final Currency USD = new Currency("USD", "United States Dollar", "United States dollar.", "USD", "$");
    public static final Currency EUR = new Currency("EUR", "Euro", "Euro.", "EUR", "\u20ac");

    private static final List<Currency> VALUES = List.of(COP, USD, EUR);

    private final String isoCode;
    private final String symbol;

    private Currency(String code, String name, String description, String isoCode, String symbol) {
        super(code, name, description);
        this.isoCode = isoCode;
        this.symbol = symbol;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public static List<Currency> values() {
        return VALUES;
    }

    /** Validated factory: returns the supported value or rejects unknown codes. */
    public static Currency fromCode(String code) {
        return DomainCatalog.lookup(VALUES, code, "Currency");
    }
}
