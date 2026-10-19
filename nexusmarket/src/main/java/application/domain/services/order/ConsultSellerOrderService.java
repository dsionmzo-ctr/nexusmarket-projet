package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.services.authorization.AuthorizeOrderAccessService;
import application.domain.valueobjects.SystemRole;

/**
 * Consults one order that contains products of the authenticated seller, restricted to the seller's lines.
 */
public class ConsultSellerOrderService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindOrderService findOrderService;
    private final AuthorizeOrderAccessService authorizeOrderAccessService;

    public ConsultSellerOrderService(AuthorizeOperationService authorizeOperationService,
            FindOrderService findOrderService,
            AuthorizeOrderAccessService authorizeOrderAccessService) {
        this.authorizeOperationService = authorizeOperationService;
        this.findOrderService = findOrderService;
        this.authorizeOrderAccessService = authorizeOrderAccessService;
    }

    public Order execute(User user, Order probe) {
        authorizeOperationService.execute(user, SystemRole.SELLER);
        return authorizeOrderAccessService.execute(user, findOrderService.execute(probe));
    }
}
