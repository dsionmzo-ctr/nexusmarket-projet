package application.domain.services.cart;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCartException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Adds a variant of a PUBLISHED product to the cart of the authenticated buyer; the quantities are added when
 * the variant is already in the cart.
 */
public class AddItemToCartService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final ProductRepositoryPort productRepositoryPort;
    private final FindOrCreateCartService findOrCreateCartService;
    private final CartRepositoryPort cartRepositoryPort;
    private final Clock clock;

    public AddItemToCartService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            ProductRepositoryPort productRepositoryPort,
            FindOrCreateCartService findOrCreateCartService,
            CartRepositoryPort cartRepositoryPort,
            Clock clock) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.productRepositoryPort = productRepositoryPort;
        this.findOrCreateCartService = findOrCreateCartService;
        this.cartRepositoryPort = cartRepositoryPort;
        this.clock = clock;
    }

    public Cart execute(User user, CartItem item) {
        Buyer buyer = resolveAuthenticatedBuyerService.execute(user);
        buyer.requireCanPurchase();
        if (item == null || item.getVariant() == null) {
            throw new InvalidCartException("The variant to add must be provided.");
        }
        if (item.getQuantity() <= 0) {
            throw new InvalidCartException("Quantity must be greater than zero.");
        }
        Product product = productRepositoryPort.findByVariant(item.getVariant())
                .filter(Product::isPublished)
                .orElseThrow(() -> new EntityNotFoundException("Product variant", item.getVariant().getId()));
        ProductVariant variant = product.findVariant(item.getVariant().getId()).orElseThrow();
        Cart cart = findOrCreateCartService.execute(buyer);
        cart.addItem(product, variant, item.getQuantity(), LocalDateTime.now(clock));
        return cartRepositoryPort.save(cart);
    }
}
