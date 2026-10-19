package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.domain.models.Seller;

/** Seller Domain Model <-> SellerJpaEntity. */
public final class SellerJpaMapper {

    private SellerJpaMapper() {
    }

    public static SellerJpaEntity toEntity(Seller domain) {
        if (domain == null) {
            return null;
        }
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setBusinessName(domain.getBusinessName());
        return entity;
    }

    public static Seller toDomain(SellerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Seller domain = new Seller();
        domain.setId(entity.getId());
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setBusinessName(entity.getBusinessName());
        return domain;
    }

    /** Identifier-only entity used to reference a seller from another aggregate. */
    public static SellerJpaEntity ref(Seller domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
