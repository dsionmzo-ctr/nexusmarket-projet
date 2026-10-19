package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Invoice;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.ProductReturn;
import application.domain.models.Refund;
import application.domain.models.ReturnRequestResult;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.Address;
import java.util.List;

/**
 * Input port: operations of the BUYER role. A buyer only handles his own profile, cart, orders,
 * returns and refunds, and never inventories or other buyers' information.
 */
public interface BuyerPort {

    Buyer consultMyProfile(User user);

    Buyer addAddress(User user, Address address);

    Buyer removeAddress(User user, Address address);

    Cart consultMyCart(User user);

    /** {@code item} carries the variant (id) and the desired quantity. */
    Cart addItemToCart(User user, CartItem item);

    Cart updateCartItem(User user, CartItem item);

    Cart removeCartItem(User user, CartItem item);

    Cart clearCart(User user);

    /** Confirms the cart: {@code order} optionally carries the shipping address; creates the order, reserves stock and empties the cart. */
    Order checkout(User user, Order order);

    /** {@code order} carries the order id and the payment method. */
    Order payOrder(User user, Order order);

    Order cancelOrder(User user, Order order);

    List<Order> consultMyOrders(User user);

    Order consultMyOrder(User user, Order order);

    Invoice consultMyInvoice(User user, Order order);

    Shipment consultMyShipment(User user, Order order);

    /** {@code productReturn} carries the order, the purchased line (variant), quantity, reason and comment. */
    ReturnRequestResult requestReturn(User user, ProductReturn productReturn);

    List<ProductReturn> consultMyReturns(User user);

    List<Refund> consultMyRefunds(User user);

    List<Operation> consultMyOperations(User user);
}
