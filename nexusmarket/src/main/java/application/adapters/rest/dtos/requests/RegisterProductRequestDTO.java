package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/** Registration of a product with its variants (created SUSPENDED until published). */
public record RegisterProductRequestDTO(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 40) String type,
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal priceAmount,
        @NotBlank @Size(max = 40) String priceCurrency,
        @NotEmpty List<@Valid ProductVariantRequestDTO> variants
) {
}
