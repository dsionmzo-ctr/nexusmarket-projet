package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.persistence.jpa.repositories.InvoiceJpaRepository;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.out.InvoiceRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the invoice repository port. */
@Repository
@Transactional
public class InvoiceJpaAdapter implements InvoiceRepositoryPort {

    private final InvoiceJpaRepository invoiceJpaRepository;

    public InvoiceJpaAdapter(InvoiceJpaRepository invoiceJpaRepository) {
        this.invoiceJpaRepository = invoiceJpaRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return InvoiceJpaMapper.toDomain(invoiceJpaRepository.save(InvoiceJpaMapper.toEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findByOrder(Order order) {
        return invoiceJpaRepository.findByOrder_Id(order.getId()).map(InvoiceJpaMapper::toDomain);
    }

    /** Correlative number derived from the next identifier value; the column is unique. */
    @Override
    public String nextInvoiceNumber() {
        return String.format("INV-%08d", invoiceJpaRepository.nextSequenceValue());
    }
}
