package application.adapters.persistence.jpa.mappers;

import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;

import java.math.BigDecimal;

/** Money Value Object <-> amount + currency code columns. */
public final class MoneyJpaMapper {

    private MoneyJpaMapper() {
    }

    public static Money toDomain(BigDecimal amount, String currencyCode) {
        if (amount == null || currencyCode == null) {
            return null;
        }
        return new Money(amount, Currency.fromCode(currencyCode));
    }
}
