package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;

/**
 * Consults one order of the marketplace (LOGISTIC_OPERATOR, ADMIN and SUPERVISOR).
 */
public class ConsultOrderService {

    private final AuthorizeOperationService authorizeOperationService;
    private final FindOrderService findOrderService;

    public ConsultOrderService(AuthorizeOperationService authorizeOperationService,
            FindOrderService findOrderService) {
        this.authorizeOperationService = authorizeOperationService;
        this.findOrderService = findOrderService;
    }

    public Order execute(User user, Order probe) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return findOrderService.execute(probe);
    }
}
