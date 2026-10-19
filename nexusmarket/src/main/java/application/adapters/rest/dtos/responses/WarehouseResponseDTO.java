package application.adapters.rest.dtos.responses;

/** Warehouse data. */
public record WarehouseResponseDTO(
        Long id,
        String name,
        String type,
        AddressResponseDTO address,
        Long sellerId,
        String status
) {
}
