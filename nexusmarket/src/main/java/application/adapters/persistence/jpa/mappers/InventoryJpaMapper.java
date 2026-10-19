package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.domain.models.Inventory;
import application.domain.valueobjects.InventoryStatus;

/** Inventory Domain Model <-> InventoryJpaEntity. */
public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static InventoryJpaEntity toEntity(Inventory domain) {
        if (domain == null) {
            return null;
        }
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setId(domain.getId());
        entity.setProduct(ProductJpaMapper.ref(domain.getProduct()));
        entity.setVariant(ProductJpaMapper.variantRef(domain.getVariant()));
        entity.setWarehouse(WarehouseJpaMapper.ref(domain.getWarehouse()));
        entity.setOnHand(domain.getOnHand());
        entity.setReserved(domain.getReserved());
        entity.setStatus(domain.getStatus().getCode());
        return entity;
    }

    public static Inventory toDomain(InventoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Inventory domain = new Inventory();
        domain.setId(entity.getId());
        domain.setProduct(ProductJpaMapper.toShallowDomain(entity.getProduct()));
        domain.setVariant(ProductJpaMapper.variantToDomain(entity.getVariant()));
        domain.setWarehouse(WarehouseJpaMapper.toDomain(entity.getWarehouse()));
        domain.setOnHand(entity.getOnHand());
        domain.setReserved(entity.getReserved());
        domain.setStatus(InventoryStatus.fromCode(entity.getStatus()));
        return domain;
    }

    public static InventoryJpaEntity ref(Inventory domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }

    /** Domain reference that only carries the identifier. */
    public static Inventory idOnly(InventoryJpaEntity entity) {
        Inventory domain = new Inventory();
        domain.setId(entity.getId());
        return domain;
    }
}
