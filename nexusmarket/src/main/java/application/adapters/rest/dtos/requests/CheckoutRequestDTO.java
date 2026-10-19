package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;

/** Optional shipping address; the buyer's primary address is used when omitted. */
public record CheckoutRequestDTO(
        @Valid AddressRequestDTO shippingAddress
) {
}
