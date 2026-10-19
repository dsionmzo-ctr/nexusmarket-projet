package application.adapters.rest.dtos.responses;

/** Stock of a variant in a warehouse. */
public record InventoryResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long variantId,
        String sku,
        Long warehouseId,
        String warehouseName,
        int onHand,
        int reserved,
        int available,
        String status
) {
}
