package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.BuyerPort;
import application.domain.services.billing.ConsultOrderInvoiceService;
import application.domain.services.buyer.AddBuyerAddressService;
import application.domain.services.buyer.ConsultBuyerProfileService;
import application.domain.services.buyer.RemoveBuyerAddressService;
import application.domain.services.cart.AddItemToCartService;
import application.domain.services.cart.ClearCartService;
import application.domain.services.cart.ConsultCartService;
import application.domain.services.cart.RemoveCartItemService;
import application.domain.services.cart.UpdateCartItemQuantityService;
import application.domain.services.operation.ConsultUserOperationsService;
import application.domain.services.order.CancelOrderService;
import application.domain.services.order.CheckoutService;
import application.domain.services.order.ConsultBuyerOrdersService;
import application.domain.services.order.FindBuyerOrderService;
import application.domain.services.order.PayOrderService;
import application.domain.services.returns.ConsultBuyerRefundsService;
import application.domain.services.returns.ConsultBuyerReturnsService;
import application.domain.services.returns.RequestReturnService;
import application.domain.services.shipping.ConsultOrderShipmentService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the Buyer role: profile, cart, orders, invoices, shipments, returns and refunds.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class BuyerUseCaseImpl implements BuyerPort {

    private final ConsultBuyerProfileService consultBuyerProfileService;
    private final AddBuyerAddressService addBuyerAddressService;
    private final RemoveBuyerAddressService removeBuyerAddressService;
    private final ConsultCartService consultCartService;
    private final AddItemToCartService addItemToCartService;
    private final UpdateCartItemQuantityService updateCartItemQuantityService;
    private final RemoveCartItemService removeCartItemService;
    private final ClearCartService clearCartService;
    private final CheckoutService checkoutService;
    private final PayOrderService payOrderService;
    private final CancelOrderService cancelOrderService;
    private final ConsultBuyerOrdersService consultBuyerOrdersService;
    private final FindBuyerOrderService findBuyerOrderService;
    private final ConsultOrderInvoiceService consultOrderInvoiceService;
    private final ConsultOrderShipmentService consultOrderShipmentService;
    private final RequestReturnService requestReturnService;
    private final ConsultBuyerReturnsService consultBuyerReturnsService;
    private final ConsultBuyerRefundsService consultBuyerRefundsService;
    private final ConsultUserOperationsService consultUserOperationsService;

    public BuyerUseCaseImpl(ConsultBuyerProfileService consultBuyerProfileService,
            AddBuyerAddressService addBuyerAddressService,
            RemoveBuyerAddressService removeBuyerAddressService,
            ConsultCartService consultCartService,
            AddItemToCartService addItemToCartService,
            UpdateCartItemQuantityService updateCartItemQuantityService,
            RemoveCartItemService removeCartItemService,
            ClearCartService clearCartService,
            CheckoutService checkoutService,
            PayOrderService payOrderService,
            CancelOrderService cancelOrderService,
            ConsultBuyerOrdersService consultBuyerOrdersService,
            FindBuyerOrderService findBuyerOrderService,
            ConsultOrderInvoiceService consultOrderInvoiceService,
            ConsultOrderShipmentService consultOrderShipmentService,
            RequestReturnService requestReturnService,
            ConsultBuyerReturnsService consultBuyerReturnsService,
            ConsultBuyerRefundsService consultBuyerRefundsService,
            ConsultUserOperationsService consultUserOperationsService) {
        this.consultBuyerProfileService = consultBuyerProfileService;
        this.addBuyerAddressService = addBuyerAddressService;
        this.removeBuyerAddressService = removeBuyerAddressService;
        this.consultCartService = consultCartService;
        this.addItemToCartService = addItemToCartService;
        this.updateCartItemQuantityService = updateCartItemQuantityService;
        this.removeCartItemService = removeCartItemService;
        this.clearCartService = clearCartService;
        this.checkoutService = checkoutService;
        this.payOrderService = payOrderService;
        this.cancelOrderService = cancelOrderService;
        this.consultBuyerOrdersService = consultBuyerOrdersService;
        this.findBuyerOrderService = findBuyerOrderService;
        this.consultOrderInvoiceService = consultOrderInvoiceService;
        this.consultOrderShipmentService = consultOrderShipmentService;
        this.requestReturnService = requestReturnService;
        this.consultBuyerReturnsService = consultBuyerReturnsService;
        this.consultBuyerRefundsService = consultBuyerRefundsService;
        this.consultUserOperationsService = consultUserOperationsService;
    }

    @Override
    public Buyer consultMyProfile(User user) {
        return consultBuyerProfileService.execute(user);
    }

    @Override
    public Buyer addAddress(User user, Address address) {
        return addBuyerAddressService.execute(user, address);
    }

    @Override
    public Buyer removeAddress(User user, Address address) {
        return removeBuyerAddressService.execute(user, address);
    }

    @Override
    public Cart consultMyCart(User user) {
        return consultCartService.execute(user);
    }

    @Override
    public Cart addItemToCart(User user, CartItem item) {
        return addItemToCartService.execute(user, item);
    }

    @Override
    public Cart updateCartItem(User user, CartItem item) {
        return updateCartItemQuantityService.execute(user, item);
    }

    @Override
    public Cart removeCartItem(User user, CartItem item) {
        return removeCartItemService.execute(user, item);
    }

    @Override
    public Cart clearCart(User user) {
        return clearCartService.execute(user);
    }

    @Override
    public Order checkout(User user, Order order) {
        return checkoutService.execute(user, order);
    }

    @Override
    public Order payOrder(User user, Order order) {
        return payOrderService.execute(user, order);
    }

    @Override
    public Order cancelOrder(User user, Order order) {
        return cancelOrderService.execute(user, order);
    }

    @Override
    public List<Order> consultMyOrders(User user) {
        return consultBuyerOrdersService.execute(user);
    }

    @Override
    public Order consultMyOrder(User user, Order order) {
        return findBuyerOrderService.execute(user, order);
    }

    @Override
    public Invoice consultMyInvoice(User user, Order order) {
        return consultOrderInvoiceService.execute(user, order);
    }

    @Override
    public Shipment consultMyShipment(User user, Order order) {
        return consultOrderShipmentService.execute(user, order);
    }

    @Override
    public ReturnRequestResult requestReturn(User user, ProductReturn productReturn) {
        return requestReturnService.execute(user, productReturn);
    }

    @Override
    public List<ProductReturn> consultMyReturns(User user) {
        return consultBuyerReturnsService.execute(user);
    }

    @Override
    public List<Refund> consultMyRefunds(User user) {
        return consultBuyerRefundsService.execute(user);
    }

    @Override
    public List<Operation> consultMyOperations(User user) {
        return consultUserOperationsService.execute(user);
    }
}
