package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.AddressEmbeddable;
import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;

import java.util.ArrayList;
import java.util.List;

/** Buyer Domain Model <-> BuyerJpaEntity. */
public final class BuyerJpaMapper {

    private BuyerJpaMapper() {
    }

    public static BuyerJpaEntity toEntity(Buyer domain) {
        if (domain == null) {
            return null;
        }
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setStatus(domain.getStatus().getCode());
        entity.setPrimaryAddress(AddressJpaMapper.toEmbeddable(domain.getPrimaryAddress()));
        List<AddressEmbeddable> additional = new ArrayList<>();
        domain.getAdditionalAddresses().forEach(a -> additional.add(AddressJpaMapper.toEmbeddable(a)));
        entity.setAdditionalAddresses(additional);
        return entity;
    }

    public static Buyer toDomain(BuyerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Buyer domain = new Buyer();
        domain.setId(entity.getId());
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setStatus(BuyerStatus.fromCode(entity.getStatus()));
        domain.setPrimaryAddress(AddressJpaMapper.toDomain(entity.getPrimaryAddress()));
        List<application.domain.valueobjects.Address> additional = new ArrayList<>();
        entity.getAdditionalAddresses().forEach(a -> additional.add(AddressJpaMapper.toDomain(a)));
        domain.setAdditionalAddresses(additional);
        return domain;
    }

    /** Identifier-only entity used to reference a buyer from another aggregate. */
    public static BuyerJpaEntity ref(Buyer domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
