package application.adapters.rest.dtos.responses;

/** Line of the cart. */
public record CartItemResponseDTO(
        Long productId,
        String productName,
        Long variantId,
        String sku,
        MoneyResponseDTO unitPrice,
        int quantity,
        MoneyResponseDTO lineTotal
) {
}
