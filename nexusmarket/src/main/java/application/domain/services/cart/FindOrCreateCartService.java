package application.domain.services.cart;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.ports.out.CartRepositoryPort;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Finds the cart of a buyer, creating an empty one the first time.
 */
public class FindOrCreateCartService {

    private final CartRepositoryPort cartRepositoryPort;
    private final Clock clock;

    public FindOrCreateCartService(CartRepositoryPort cartRepositoryPort,
            Clock clock) {
        this.cartRepositoryPort = cartRepositoryPort;
        this.clock = clock;
    }

    public Cart execute(Buyer buyer) {
        return cartRepositoryPort.findByBuyer(buyer).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setBuyer(buyer);
            cart.setUpdatedAt(LocalDateTime.now(clock));
            return cartRepositoryPort.save(cart);
        });
    }
}
