package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.CreateWarehouseRequestDTO;
import application.adapters.rest.dtos.requests.UpdateWarehouseRequestDTO;
import application.adapters.rest.dtos.responses.WarehouseResponseDTO;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

/** Warehouse Domain Model <-> warehouse DTOs. */
public final class WarehouseRestMapper {

    private WarehouseRestMapper() {
    }

    public static Warehouse toDomain(CreateWarehouseRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.name().trim());
        warehouse.setType(WarehouseType.fromCode(dto.type()));
        warehouse.setAddress(AddressRestMapper.toDomain(dto.address()));
        if (dto.sellerId() != null) {
            Seller seller = new Seller();
            seller.setId(dto.sellerId());
            warehouse.setSeller(seller);
        }
        return warehouse;
    }

    public static Warehouse toChanges(Long warehouseId, UpdateWarehouseRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setName(dto.name().trim());
        warehouse.setAddress(AddressRestMapper.toDomain(dto.address()));
        return warehouse;
    }

    public static Warehouse toProbe(Long warehouseId) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        return warehouse;
    }

    public static WarehouseStatus toStatus(String code) {
        return WarehouseStatus.fromCode(code);
    }

    public static WarehouseResponseDTO toResponse(Warehouse warehouse) {
        return new WarehouseResponseDTO(warehouse.getId(), warehouse.getName(), warehouse.getType().getCode(),
                AddressRestMapper.toResponse(warehouse.getAddress()),
                warehouse.getSeller() == null ? null : warehouse.getSeller().getId(),
                warehouse.getStatus().getCode());
    }
}
