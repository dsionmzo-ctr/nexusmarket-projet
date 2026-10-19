package application.domain.services.billing;

import application.domain.models.Invoice;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Issues the invoice of a paid order (subtotal + sales tax).
 */
public class GenerateInvoiceService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public GenerateInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort,
            BusinessConfigurationPort businessConfigurationPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Invoice execute(User actor, Order order) {
        LocalDateTime now = LocalDateTime.now(clock);
        Invoice invoice = Invoice.issueFor(order, businessConfigurationPort.getTaxRate(),
                invoiceRepositoryPort.nextInvoiceNumber(), now);
        Invoice saved = invoiceRepositoryPort.save(invoice);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.INVOICE_ISSUANCE, actor, saved, now),
                AuditDetails.of("invoiceNumber", saved.getInvoiceNumber(), "orderId", order.getId(),
                        "total", saved.getTotal().amount().toPlainString()));
        return saved;
    }
}
