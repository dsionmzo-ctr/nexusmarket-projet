package application.domain.services.order;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import application.domain.services.inventory.ReserveStockService;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Confirms the cart of the authenticated buyer: every line is validated against the catalog, the stock of
 * physical products is reserved, the order is created in PENDING_PAYMENT and the cart is emptied.
 */
public class CheckoutService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ReserveStockService reserveStockService;
    private final OrderRepositoryPort orderRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public CheckoutService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            CartRepositoryPort cartRepositoryPort,
            ProductRepositoryPort productRepositoryPort,
            ReserveStockService reserveStockService,
            OrderRepositoryPort orderRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.reserveStockService = reserveStockService;
        this.orderRepositoryPort = orderRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Order execute(User user, Order requested) {
        Buyer buyer = resolveAuthenticatedBuyerService.execute(user);
        buyer.requireCanPurchase();
        Cart cart = cartRepositoryPort.findByBuyer(buyer)
                .filter(c -> !c.isEmpty())
                .orElseThrow(() -> new InvalidOrderException("The cart is empty."));
        Address requestedAddress = requested == null ? null : requested.getShippingAddress();
        Address shipping = requestedAddress == null ? buyer.getPrimaryAddress() : requestedAddress;
        if (!buyer.ownsAddress(shipping)) {
            throw new DomainException("The shipping address does not belong to the buyer.");
        }

        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepositoryPort.findByVariant(cartItem.getVariant())
                    .orElseThrow(() -> new InvalidOrderException("A product of the cart no longer exists."));
            if (!product.isPublished()) {
                throw new InvalidOrderException("Product '" + product.getName() + "' is not available for purchase.");
            }
            ProductVariant variant = product.findVariant(cartItem.getVariant().getId()).orElseThrow();
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setVariant(variant);
            item.setSeller(product.getSeller());
            item.setProductName(product.getName());
            item.setSku(variant.getSku());
            item.setProductType(product.getType());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(cartItem.getQuantity());
            if (product.requiresInventory()) {
                item.setAllocations(reserveStockService.execute(user, variant, cartItem.getQuantity()));
            }
            items.add(item);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        Order saved = orderRepositoryPort.save(Order.place(buyer, items, shipping, now));
        cart.clear(now);
        cartRepositoryPort.save(cart);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.ORDER_CREATION, user, saved, now),
                AuditDetails.of("total", saved.total().amount().toPlainString(),
                        "currency", saved.total().currency().getCode(), "items", saved.getItems().size()));
        return saved;
    }
}
