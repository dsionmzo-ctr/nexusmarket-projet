package application.adapters.rest.dtos.responses;

/** Seller profile. */
public record SellerResponseDTO(
        Long id,
        String identification,
        String name,
        String email,
        String businessName
) {
}
