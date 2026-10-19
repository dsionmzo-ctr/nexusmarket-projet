package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults every order of the marketplace (LOGISTIC_OPERATOR, ADMIN and SUPERVISOR).
 */
public class ConsultAllOrdersService {

    private final AuthorizeOperationService authorizeOperationService;
    private final OrderRepositoryPort orderRepositoryPort;

    public ConsultAllOrdersService(AuthorizeOperationService authorizeOperationService,
            OrderRepositoryPort orderRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public List<Order> execute(User user) {
        authorizeOperationService.execute(user, SystemRole.LOGISTIC_OPERATOR, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return orderRepositoryPort.findAll();
    }
}
