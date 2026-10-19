package application.domain.services.cart;

import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;

/**
 * Consults the cart of the authenticated buyer.
 */
public class ConsultCartService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final FindOrCreateCartService findOrCreateCartService;

    public ConsultCartService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            FindOrCreateCartService findOrCreateCartService) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.findOrCreateCartService = findOrCreateCartService;
    }

    public Cart execute(User user) {
        return findOrCreateCartService.execute(resolveAuthenticatedBuyerService.execute(user));
    }
}
