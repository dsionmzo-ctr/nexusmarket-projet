package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.domain.models.Refund;
import application.domain.valueobjects.RefundStatus;

/** Refund Domain Model <-> RefundJpaEntity. */
public final class RefundJpaMapper {

    private RefundJpaMapper() {
    }

    public static RefundJpaEntity toEntity(Refund domain) {
        RefundJpaEntity entity = new RefundJpaEntity();
        entity.setId(domain.getId());
        entity.setProductReturn(ProductReturnJpaMapper.ref(domain.getProductReturn()));
        entity.setAmount(domain.getAmount().amount());
        entity.setCurrency(domain.getAmount().currency().getCode());
        entity.setStatus(domain.getStatus().getCode());
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setDecidedAt(domain.getDecidedAt());
        entity.setDecidedBy(UserJpaMapper.ref(domain.getDecidedBy()));
        entity.setDecisionNote(domain.getDecisionNote());
        return entity;
    }

    public static Refund toDomain(RefundJpaEntity entity) {
        Refund domain = new Refund();
        domain.setId(entity.getId());
        domain.setProductReturn(ProductReturnJpaMapper.toDomain(entity.getProductReturn()));
        domain.setAmount(MoneyJpaMapper.toDomain(entity.getAmount(), entity.getCurrency()));
        domain.setStatus(RefundStatus.fromCode(entity.getStatus()));
        domain.setRequestedAt(entity.getRequestedAt());
        domain.setDecidedAt(entity.getDecidedAt());
        domain.setDecidedBy(UserJpaMapper.toShallowDomain(entity.getDecidedBy()));
        domain.setDecisionNote(entity.getDecisionNote());
        return domain;
    }
}
