package application.adapters.rest.dtos.responses;

/** Line of an order (price and names are a snapshot of the purchase). */
public record OrderItemResponseDTO(
        Long id,
        Long productId,
        Long variantId,
        Long sellerId,
        String productName,
        String sku,
        String productType,
        MoneyResponseDTO unitPrice,
        int quantity,
        MoneyResponseDTO lineTotal
) {
}
