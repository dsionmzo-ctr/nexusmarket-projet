package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;

/** Amount with its currency code. */
public record MoneyResponseDTO(
        BigDecimal amount,
        String currency
) {
}
