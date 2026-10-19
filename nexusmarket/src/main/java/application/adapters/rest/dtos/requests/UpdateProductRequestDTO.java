package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Editable fields of a product; at least one must be present. */
public record UpdateProductRequestDTO(
        @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @Positive @Digits(integer = 17, fraction = 2) BigDecimal priceAmount,
        @Size(max = 40) String priceCurrency
) {
    @AssertTrue(message = "at least one field must be provided")
    public boolean isAtLeastOneFieldPresent() {
        return (name != null && !name.isBlank()) || description != null || priceAmount != null;
    }

    @AssertTrue(message = "priceCurrency is required together with priceAmount")
    public boolean isPriceConsistent() {
        return priceAmount == null || (priceCurrency != null && !priceCurrency.isBlank());
    }
}
