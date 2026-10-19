package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Shipment;
import application.domain.valueobjects.ShipmentStatus;

/** Shipment Domain Model <-> ShipmentJpaEntity. */
public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static ShipmentJpaEntity toEntity(Shipment domain) {
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setId(domain.getId());
        entity.setOrder(OrderJpaMapper.ref(domain.getOrder()));
        entity.setTrackingNumber(domain.getTrackingNumber());
        entity.setDestination(AddressJpaMapper.toEmbeddable(domain.getDestination()));
        entity.setStatus(domain.getStatus().getCode());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setShippedAt(domain.getShippedAt());
        entity.setDeliveredAt(domain.getDeliveredAt());
        entity.setHandledBy(UserJpaMapper.ref(domain.getHandledBy()));
        return entity;
    }

    public static Shipment toDomain(ShipmentJpaEntity entity) {
        Shipment domain = new Shipment();
        domain.setId(entity.getId());
        domain.setOrder(OrderJpaMapper.toDomain(entity.getOrder()));
        domain.setTrackingNumber(entity.getTrackingNumber());
        domain.setDestination(AddressJpaMapper.toDomain(entity.getDestination()));
        domain.setStatus(ShipmentStatus.fromCode(entity.getStatus()));
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setShippedAt(entity.getShippedAt());
        domain.setDeliveredAt(entity.getDeliveredAt());
        domain.setHandledBy(UserJpaMapper.toShallowDomain(entity.getHandledBy()));
        return domain;
    }
}
