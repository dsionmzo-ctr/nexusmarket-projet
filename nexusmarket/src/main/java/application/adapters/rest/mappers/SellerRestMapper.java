package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.FirstWarehouseRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.domain.models.Seller;
import application.domain.models.Warehouse;

/** Seller Domain Model <-> seller DTOs. */
public final class SellerRestMapper {

    private SellerRestMapper() {
    }

    public static Seller toDomain(RegisterSellerRequestDTO dto) {
        Seller seller = new Seller();
        seller.setIdentification(dto.identification().trim());
        seller.setName(dto.name().trim());
        seller.setEmail(dto.email().trim());
        seller.setBusinessName(dto.businessName().trim());
        return seller;
    }

    /** First warehouse of a new seller; its type and owner are decided by the incorporation service. */
    public static Warehouse toFirstWarehouse(FirstWarehouseRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.name().trim());
        warehouse.setAddress(AddressRestMapper.toDomain(dto.address()));
        return warehouse;
    }

    public static SellerResponseDTO toResponse(Seller seller) {
        if (seller == null) {
            return null;
        }
        return new SellerResponseDTO(seller.getId(), seller.getIdentification(), seller.getName(),
                seller.getEmail(), seller.getBusinessName());
    }
}
