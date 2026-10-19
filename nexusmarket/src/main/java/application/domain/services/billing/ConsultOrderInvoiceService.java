package application.domain.services.billing;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.order.FindBuyerOrderService;

/**
 * Consults the invoice of an order of the authenticated buyer.
 */
public class ConsultOrderInvoiceService {

    private final FindBuyerOrderService findBuyerOrderService;
    private final InvoiceRepositoryPort invoiceRepositoryPort;

    public ConsultOrderInvoiceService(FindBuyerOrderService findBuyerOrderService,
            InvoiceRepositoryPort invoiceRepositoryPort) {
        this.findBuyerOrderService = findBuyerOrderService;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    public Invoice execute(User user, Order probe) {
        Order order = findBuyerOrderService.execute(user, probe);
        return invoiceRepositoryPort.findByOrder(order)
                .orElseThrow(() -> new EntityNotFoundException("Invoice of order", order.getId()));
    }
}
