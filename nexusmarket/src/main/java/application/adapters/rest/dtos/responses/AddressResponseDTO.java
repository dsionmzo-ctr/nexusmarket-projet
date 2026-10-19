package application.adapters.rest.dtos.responses;

/** Postal address. */
public record AddressResponseDTO(
        String street,
        String city,
        String country
) {
}
