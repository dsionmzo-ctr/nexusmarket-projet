package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.MoneyResponseDTO;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;

import java.math.BigDecimal;

/** Money Value Object <-> money DTO. */
public final class MoneyRestMapper {

    private MoneyRestMapper() {
    }

    public static Money toDomain(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return null;
        }
        return Money.of(amount, Currency.fromCode(currencyCode));
    }

    public static MoneyResponseDTO toResponse(Money money) {
        if (money == null) {
            return null;
        }
        return new MoneyResponseDTO(money.amount(), money.currency().getCode());
    }
}
