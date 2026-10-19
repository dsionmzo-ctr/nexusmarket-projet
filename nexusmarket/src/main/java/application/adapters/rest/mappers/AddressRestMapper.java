package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.AddressRequestDTO;
import application.adapters.rest.dtos.responses.AddressResponseDTO;
import application.domain.valueobjects.Address;

/** Address Value Object <-> address DTOs. */
public final class AddressRestMapper {

    private AddressRestMapper() {
    }

    public static Address toDomain(AddressRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Address(dto.street().trim(), dto.city().trim(), dto.country().trim());
    }

    public static AddressResponseDTO toResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponseDTO(address.street(), address.city(), address.country());
    }
}
