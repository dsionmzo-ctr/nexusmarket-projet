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
 * Changes the quantity of a variant already in the cart of the authenticated buyer.
 */
public class UpdateCartItemQuantityService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final FindOrCreateCartService findOrCreateCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final Clock clock;

    public UpdateCartItemQuantityService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
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
        cart.updateQuantity(item.getVariant().getId(), item.getQuantity(), LocalDateTime.now(clock));
        return cartRepositoryPort.save(cart);
    }
}
