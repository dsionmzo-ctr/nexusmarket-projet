package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.domain.models.Invoice;
import application.domain.valueobjects.Money;

/** Invoice Domain Model <-> InvoiceJpaEntity. */
public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static InvoiceJpaEntity toEntity(Invoice domain) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setId(domain.getId());
        entity.setInvoiceNumber(domain.getInvoiceNumber());
        entity.setOrder(OrderJpaMapper.ref(domain.getOrder()));
        entity.setSubtotalAmount(domain.getSubtotal().amount());
        entity.setTaxAmount(domain.getTaxAmount().amount());
        entity.setTotalAmount(domain.getTotal().amount());
        entity.setCurrency(domain.getTotal().currency().getCode());
        entity.setIssuedAt(domain.getIssuedAt());
        return entity;
    }

    public static Invoice toDomain(InvoiceJpaEntity entity) {
        Invoice domain = new Invoice();
        domain.setId(entity.getId());
        domain.setInvoiceNumber(entity.getInvoiceNumber());
        domain.setOrder(OrderJpaMapper.toDomain(entity.getOrder()));
        domain.setSubtotal(new Money(entity.getSubtotalAmount(), application.domain.valueobjects.Currency.fromCode(entity.getCurrency())));
        domain.setTaxAmount(new Money(entity.getTaxAmount(), application.domain.valueobjects.Currency.fromCode(entity.getCurrency())));
        domain.setTotal(new Money(entity.getTotalAmount(), application.domain.valueobjects.Currency.fromCode(entity.getCurrency())));
        domain.setIssuedAt(entity.getIssuedAt());
        return domain;
    }
}
