package application.domain.services.order;

import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.billing.GenerateInvoiceService;
import application.domain.services.inventory.ConfirmStockSaleService;
import application.domain.services.notification.NotifyPersonService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.shipping.CreateShipmentService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Registers the payment of an order of the authenticated buyer and starts the post-payment flow: reserved
 * stock becomes a SALE, the invoice is issued and, for physical products, the shipment is opened. Orders
 * made only of digital products are delivered immediately.
 */
public class PayOrderService {

    private final FindBuyerOrderService findBuyerOrderService;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ConfirmStockSaleService confirmStockSaleService;
    private final GenerateInvoiceService generateInvoiceService;
    private final CreateShipmentService createShipmentService;
    private final NotifyPersonService notifyPersonService;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public PayOrderService(FindBuyerOrderService findBuyerOrderService,
            OrderRepositoryPort orderRepositoryPort,
            ConfirmStockSaleService confirmStockSaleService,
            GenerateInvoiceService generateInvoiceService,
            CreateShipmentService createShipmentService,
            NotifyPersonService notifyPersonService,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findBuyerOrderService = findBuyerOrderService;
        this.orderRepositoryPort = orderRepositoryPort;
        this.confirmStockSaleService = confirmStockSaleService;
        this.generateInvoiceService = generateInvoiceService;
        this.createShipmentService = createShipmentService;
        this.notifyPersonService = notifyPersonService;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Order execute(User user, Order requested) {
        Order order = findBuyerOrderService.execute(user, requested);
        LocalDateTime now = LocalDateTime.now(clock);
        order.markPaid(requested.getPaymentMethod(), now);
        confirmStockSaleService.execute(user, order);
        orderRepositoryPort.update(order);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.ORDER_PAYMENT, user, order, now),
                AuditDetails.of("paymentMethod", order.getPaymentMethod().getCode(),
                        "total", order.total().amount().toPlainString()));
        generateInvoiceService.execute(user, order);
        if (order.hasPhysicalItems()) {
            createShipmentService.execute(user, order);
        } else {
            order.markDelivered(now);
            orderRepositoryPort.update(order);
            registerOperationAndAuditService.execute(
                    Operation.of(OperationType.ORDER_DELIVERY, user, order, now),
                    AuditDetails.of("digitalOnly", true));
        }
        notifyPersonService.execute(order.getBuyer(), "Payment received",
                "The payment of order " + order.getId() + " was received.");
        return order;
    }
}
