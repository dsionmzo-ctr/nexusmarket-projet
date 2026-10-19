package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.domain.models.InventoryMovement;
import application.domain.valueobjects.MovementType;

/** InventoryMovement Domain Model <-> InventoryMovementJpaEntity. */
public final class InventoryMovementJpaMapper {

    private InventoryMovementJpaMapper() {
    }

    public static InventoryMovementJpaEntity toEntity(InventoryMovement domain) {
        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity();
        entity.setId(domain.getId());
        entity.setInventory(InventoryJpaMapper.ref(domain.getInventory()));
        entity.setType(domain.getType().getCode());
        entity.setQuantity(domain.getQuantity());
        entity.setOnHandAfter(domain.getOnHandAfter());
        entity.setPerformedBy(UserJpaMapper.ref(domain.getPerformedBy()));
        entity.setOccurredAt(domain.getOccurredAt());
        entity.setNote(domain.getNote());
        return entity;
    }

    public static InventoryMovement toDomain(InventoryMovementJpaEntity entity) {
        InventoryMovement domain = new InventoryMovement();
        domain.setId(entity.getId());
        domain.setInventory(InventoryJpaMapper.toDomain(entity.getInventory()));
        domain.setType(MovementType.fromCode(entity.getType()));
        domain.setQuantity(entity.getQuantity());
        domain.setOnHandAfter(entity.getOnHandAfter());
        domain.setPerformedBy(UserJpaMapper.toShallowDomain(entity.getPerformedBy()));
        domain.setOccurredAt(entity.getOccurredAt());
        domain.setNote(entity.getNote());
        return domain;
    }
}
