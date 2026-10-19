package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidReturnException;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.ProductReturn;
import application.domain.models.Refund;
import application.domain.models.ReturnRequestResult;
import application.domain.models.User;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.order.FindBuyerOrderService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * The authenticated buyer requests the return of a delivered physical product and, with it, the refund of
 * its price. The delivered order itself is never modified (finalized orders are immutable).
 */
public class RequestReturnService {

    private final FindBuyerOrderService findBuyerOrderService;
    private final ProductReturnRepositoryPort productReturnRepositoryPort;
    private final RefundRepositoryPort refundRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RequestReturnService(FindBuyerOrderService findBuyerOrderService,
            ProductReturnRepositoryPort productReturnRepositoryPort,
            RefundRepositoryPort refundRepositoryPort,
            BusinessConfigurationPort businessConfigurationPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findBuyerOrderService = findBuyerOrderService;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
        this.refundRepositoryPort = refundRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public ReturnRequestResult execute(User user, ProductReturn requested) {
        if (requested == null || requested.getOrderItem() == null || requested.getOrderItem().getVariant() == null) {
            throw new InvalidReturnException("The order and the purchased variant must be provided.");
        }
        Order order = findBuyerOrderService.execute(user, requested.getOrder());
        if (!OrderStatus.DELIVERED.equals(order.getStatus())) {
            throw new InvalidReturnException("Only delivered orders can have returns.");
        }
        Long variantId = requested.getOrderItem().getVariant().getId();
        OrderItem item = order.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order item with variant", variantId));
        if (!item.requiresShipping()) {
            throw new InvalidReturnException("Digital products cannot be returned.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime deadline = order.getDeliveredAt().plusDays(businessConfigurationPort.getReturnWindowDays());
        if (now.isAfter(deadline)) {
            throw new InvalidReturnException("The return window of the order has expired.");
        }
        int alreadyRequested = productReturnRepositoryPort.findByOrder(order).stream()
                .filter(r -> r.getOrderItem().getVariant().getId().equals(variantId)
                        && !ReturnStatus.REJECTED.equals(r.getStatus()))
                .mapToInt(ProductReturn::getQuantity)
                .sum();
        if (alreadyRequested + requested.getQuantity() > item.getQuantity()) {
            throw new InvalidReturnException("The requested quantity exceeds the purchased quantity.");
        }
        ProductReturn productReturn = ProductReturn.request(order, item, requested.getQuantity(),
                requested.getReason(), requested.getComment(), now);
        ProductReturn savedReturn = productReturnRepositoryPort.save(productReturn);
        Refund refund = Refund.request(savedReturn, item.getUnitPrice().multiply(requested.getQuantity()), now);
        Refund savedRefund = refundRepositoryPort.save(refund);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.RETURN_REQUEST, user, savedReturn, now),
                AuditDetails.of("orderId", order.getId(), "quantity", requested.getQuantity(),
                        "reason", requested.getReason().getCode(), "refundId", savedRefund.getId()));
        return new ReturnRequestResult(savedReturn, savedRefund);
    }
}
