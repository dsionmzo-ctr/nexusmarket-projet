package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;

/** Warehouse Domain Model <-> WarehouseJpaEntity. */
public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static WarehouseJpaEntity toEntity(Warehouse domain) {
        if (domain == null) {
            return null;
        }
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(domain.getType().getCode());
        entity.setAddress(AddressJpaMapper.toEmbeddable(domain.getAddress()));
        entity.setSeller(SellerJpaMapper.ref(domain.getSeller()));
        entity.setStatus(domain.getStatus().getCode());
        return entity;
    }

    public static Warehouse toDomain(WarehouseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Warehouse domain = new Warehouse();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setType(WarehouseType.fromCode(entity.getType()));
        domain.setAddress(AddressJpaMapper.toDomain(entity.getAddress()));
        domain.setSeller(SellerJpaMapper.toDomain(entity.getSeller()));
        domain.setStatus(WarehouseStatus.fromCode(entity.getStatus()));
        return domain;
    }

    public static WarehouseJpaEntity ref(Warehouse domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
