package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.responses.AddressResponseDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;

import java.util.List;

/** Buyer Domain Model <-> buyer DTOs. */
public final class BuyerRestMapper {

    private BuyerRestMapper() {
    }

    public static Buyer toDomain(RegisterBuyerRequestDTO dto) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(dto.identification().trim());
        buyer.setName(dto.name().trim());
        buyer.setEmail(dto.email().trim());
        buyer.setPrimaryAddress(AddressRestMapper.toDomain(dto.primaryAddress()));
        return buyer;
    }

    public static Buyer toProbe(Long buyerId) {
        Buyer buyer = new Buyer();
        buyer.setId(buyerId);
        return buyer;
    }

    public static BuyerStatus toStatus(String code) {
        return BuyerStatus.fromCode(code);
    }

    public static BuyerResponseDTO toResponse(Buyer buyer) {
        List<AddressResponseDTO> additional = buyer.getAdditionalAddresses().stream()
                .map(AddressRestMapper::toResponse).toList();
        return new BuyerResponseDTO(buyer.getId(), buyer.getIdentification(), buyer.getName(), buyer.getEmail(),
                buyer.getStatus().getCode(), AddressRestMapper.toResponse(buyer.getPrimaryAddress()), additional);
    }
}
