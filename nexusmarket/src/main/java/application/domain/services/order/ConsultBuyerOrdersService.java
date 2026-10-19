package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import java.util.List;

/**
 * Consults the orders of the authenticated buyer.
 */
public class ConsultBuyerOrdersService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final OrderRepositoryPort orderRepositoryPort;

    public ConsultBuyerOrdersService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            OrderRepositoryPort orderRepositoryPort) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public List<Order> execute(User user) {
        return orderRepositoryPort.findByBuyer(resolveAuthenticatedBuyerService.execute(user));
    }
}
