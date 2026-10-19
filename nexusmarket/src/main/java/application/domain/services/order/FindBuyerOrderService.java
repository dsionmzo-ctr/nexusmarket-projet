package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.services.authorization.ValidateBuyerOwnershipService;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;

/**
 * Loads an order and verifies that it belongs to the authenticated buyer.
 */
public class FindBuyerOrderService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final FindOrderService findOrderService;
    private final ValidateBuyerOwnershipService validateBuyerOwnershipService;

    public FindBuyerOrderService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            FindOrderService findOrderService,
            ValidateBuyerOwnershipService validateBuyerOwnershipService) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.findOrderService = findOrderService;
        this.validateBuyerOwnershipService = validateBuyerOwnershipService;
    }

    public Order execute(User user, Order probe) {
        Buyer buyer = resolveAuthenticatedBuyerService.execute(user);
        Order order = findOrderService.execute(probe);
        validateBuyerOwnershipService.execute(user, order.getBuyer());
        if (!order.belongsTo(buyer)) {
            throw new EntityNotFoundException("Order", probe.getId());
        }
        return order;
    }
}
