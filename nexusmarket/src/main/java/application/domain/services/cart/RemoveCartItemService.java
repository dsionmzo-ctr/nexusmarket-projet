package application.domain.services.cart;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Removes a variant from the cart of the authenticated buyer.
 */
public class RemoveCartItemService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final FindOrCreateCartService findOrCreateCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final Clock clock;

    public RemoveCartItemService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            FindOrCreateCartService findOrCreateCartService,
            CartRepositoryPort cartRepositoryPort,
            Clock clock) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.findOrCreateCartService = findOrCreateCartService;
        this.cartRepositoryPort = cartRepositoryPort;
        this.clock = clock;
    }

    public Cart execute(User user, CartItem item) {
        Buyer buyer = resolveAuthenticatedBuyerService.execute(user);
        Cart cart = findOrCreateCartService.execute(buyer);
        cart.removeItem(item.getVariant().getId(), LocalDateTime.now(clock));
        return cartRepositoryPort.save(cart);
    }
}
