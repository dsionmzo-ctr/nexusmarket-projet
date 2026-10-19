package application.adapters.rest.dtos.responses;

import java.util.List;

/** Buyer profile. */
public record BuyerResponseDTO(
        Long id,
        String identification,
        String name,
        String email,
        String status,
        AddressResponseDTO primaryAddress,
        List<AddressResponseDTO> additionalAddresses
) {
}
