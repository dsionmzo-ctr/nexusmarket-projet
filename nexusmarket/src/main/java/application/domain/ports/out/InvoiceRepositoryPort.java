package application.domain.ports.out;

import application.domain.models.Invoice;
import application.domain.models.Order;
import java.util.Optional;

/**
 * Output port: persistence of invoices (immutable once issued).
 */
public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    Optional<Invoice> findByOrder(Order order);

    /** Next consecutive invoice number, as an external sequence concern. */
    String nextInvoiceNumber();
}
