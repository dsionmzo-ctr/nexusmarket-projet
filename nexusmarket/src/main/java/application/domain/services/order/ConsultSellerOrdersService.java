package application.domain.services.order;

import application.domain.models.Order;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.seller.ResolveAuthenticatedSellerService;
import java.util.List;

/**
 * Consults the orders that contain products of the authenticated seller, restricted to the seller's lines.
 */
public class ConsultSellerOrdersService {

    private final ResolveAuthenticatedSellerService resolveAuthenticatedSellerService;
    private final OrderRepositoryPort orderRepositoryPort;

    public ConsultSellerOrdersService(ResolveAuthenticatedSellerService resolveAuthenticatedSellerService,
            OrderRepositoryPort orderRepositoryPort) {
        this.resolveAuthenticatedSellerService = resolveAuthenticatedSellerService;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public List<Order> execute(User user) {
        Seller seller = resolveAuthenticatedSellerService.execute(user);
        return orderRepositoryPort.findBySeller(seller).stream().map(o -> o.forSeller(seller)).toList();
    }
}
