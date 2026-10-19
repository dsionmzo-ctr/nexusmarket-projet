package application.domain;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.ImmutableOrderException;
import application.domain.exceptions.InvalidCartException;
import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Shipment;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ShipmentStatus;
import application.support.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Cart, order, billing and shipping domains: the complete purchase flow of section 6.1. */
class OrderLifecycleTest {

    private Scenario scenario;
    private Product phone;
    private Product ebook;

    @BeforeEach
    void setUp() {
        scenario = new Scenario();
        phone = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 10, "PH-1", "PH-2");
        ebook = scenario.publishedDigitalProduct(scenario.sellerUser, "E-book", "50000", "EB-1");
    }

    private Inventory stockOf(String sku) {
        return scenario.container.inventories.findAll().stream()
                .filter(i -> i.getVariant().getSku().equals(sku)).findFirst().orElseThrow();
    }

    @Test
    void cartAccumulatesQuantitiesAndComputesLines() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Cart cart = scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 2));
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());

        cart = scenario.buyerPort.updateCartItem(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());

        cart = scenario.buyerPort.removeCartItem(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 0));
        assertTrue(cart.isEmpty());
        assertTrue(scenario.buyerPort.clearCart(scenario.buyerUser).isEmpty());
    }

    @Test
    void onlyPublishedProductsCanBeAddedToTheCart() {
        Product hidden = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Hidden", application.domain.valueobjects.ProductType.PHYSICAL, "1000", "HID-1"));
        assertThrows(EntityNotFoundException.class,
                () -> scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(hidden.getVariants().get(0), 1)));
        assertThrows(InvalidCartException.class,
                () -> scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 0)));
    }

    @Test
    void checkoutReservesStockClearsTheCartAndLeavesTheOrderPendingPayment() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 2));
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(ebook.getVariants().get(0), 1));

        Order order = scenario.buyerPort.checkout(scenario.buyerUser, null);

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("2050000.00"), order.total().amount());
        assertEquals(scenario.buyerUser.getBuyer().getPrimaryAddress(), order.getShippingAddress());
        assertEquals(2, stockOf("PH-1").getReserved());
        assertEquals(8, stockOf("PH-1").available());
        assertTrue(scenario.buyerPort.consultMyCart(scenario.buyerUser).isEmpty());
    }

    @Test
    void checkoutOfAnEmptyCartIsRejected() {
        assertThrows(InvalidOrderException.class, () -> scenario.buyerPort.checkout(scenario.buyerUser, null));
    }

    @Test
    void shippingAddressMustBelongToTheBuyer() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order requested = new Order();
        requested.setShippingAddress(new Address("Somewhere else 1", "Bogota", "Colombia"));
        assertThrows(RuntimeException.class, () -> scenario.buyerPort.checkout(scenario.buyerUser, requested));

        scenario.buyerPort.addAddress(scenario.buyerUser, new Address("Somewhere else 1", "Bogota", "Colombia"));
        Order order = scenario.buyerPort.checkout(scenario.buyerUser, requested);
        assertEquals("Bogota", order.getShippingAddress().city());
    }

    @Test
    void restrictedBuyersCannotPurchase() {
        scenario.adminPort.changeBuyerStatus(scenario.admin, scenario.buyerUser.getBuyer(), BuyerStatus.RESTRICTED);
        assertThrows(RuntimeException.class,
                () -> scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1)));
    }

    @Test
    void payingAPhysicalOrderIssuesTheInvoiceAndPreparesTheShipment() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 2));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);

        Order paid = scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));

        assertEquals(OrderStatus.PAID, paid.getStatus());
        assertEquals(PaymentMethod.CREDIT_CARD, paid.getPaymentMethod());
        assertNotNull(paid.getPaidAt());
        Inventory stock = stockOf("PH-1");
        assertEquals(8, stock.getOnHand());
        assertEquals(0, stock.getReserved());

        Invoice invoice = scenario.buyerPort.consultMyInvoice(scenario.buyerUser, pending);
        assertEquals("INV-00000001", invoice.getInvoiceNumber());
        assertEquals(new BigDecimal("2000000.00"), invoice.getSubtotal().amount());
        assertEquals(new BigDecimal("380000.00"), invoice.getTaxAmount().amount());
        assertEquals(new BigDecimal("2380000.00"), invoice.getTotal().amount());

        Shipment shipment = scenario.buyerPort.consultMyShipment(scenario.buyerUser, pending);
        assertEquals(ShipmentStatus.PREPARING, shipment.getStatus());
        assertEquals(paid.getShippingAddress(), shipment.getDestination());
        assertNotNull(shipment.getTrackingNumber());
        assertEquals(1, scenario.container.notifications.sent.size());
    }

    @Test
    void digitalOnlyOrdersAreDeliveredImmediatelyAfterPaymentWithoutShipment() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(ebook.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        Order delivered = scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.DIGITAL_WALLET));
        assertEquals(OrderStatus.DELIVERED, delivered.getStatus());
        assertNotNull(delivered.getDeliveredAt());
        assertTrue(scenario.container.shipments.findByStatus(new Shipment()).isEmpty());
        assertThrows(EntityNotFoundException.class, () -> scenario.buyerPort.consultMyShipment(scenario.buyerUser, pending));
    }

    @Test
    void fullLogisticFlowEndsWithAFinalizedOrder() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.BANK_TRANSFER));

        Shipment filter = new Shipment();
        filter.setStatus(ShipmentStatus.PREPARING);
        List<Shipment> toDispatch = scenario.logisticPort.consultShipments(scenario.operator, filter);
        assertEquals(1, toDispatch.size());

        Shipment dispatched = scenario.logisticPort.dispatchShipment(scenario.operator, toDispatch.get(0));
        assertEquals(ShipmentStatus.SHIPPED, dispatched.getStatus());
        assertEquals(OrderStatus.SHIPPED, scenario.buyerPort.consultMyOrder(scenario.buyerUser, pending).getStatus());

        Shipment delivered = scenario.logisticPort.confirmDelivery(scenario.operator, dispatched);
        assertEquals(ShipmentStatus.DELIVERED, delivered.getStatus());
        assertEquals(OrderStatus.DELIVERED, scenario.buyerPort.consultMyOrder(scenario.buyerUser, pending).getStatus());
    }

    @Test
    void shipmentsFollowTheirStateMachine() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));
        Shipment shipment = scenario.buyerPort.consultMyShipment(scenario.buyerUser, pending);

        assertThrows(InvalidStatusTransitionException.class, () -> scenario.logisticPort.confirmDelivery(scenario.operator, shipment));
        scenario.logisticPort.dispatchShipment(scenario.operator, shipment);
        assertThrows(InvalidStatusTransitionException.class, () -> scenario.logisticPort.dispatchShipment(scenario.operator, shipment));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.logisticPort.dispatchShipment(scenario.buyerUser, shipment));
    }

    @Test
    void cancellingAPendingOrderReleasesTheReservedStock() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 3));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        assertEquals(3, stockOf("PH-1").getReserved());

        Order cancelled = scenario.buyerPort.cancelOrder(scenario.buyerUser, pending);

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertEquals(0, stockOf("PH-1").getReserved());
        assertEquals(10, stockOf("PH-1").getOnHand());
    }

    @Test
    void paidOrdersCannotBeCancelledAndFinalizedOrdersAreImmutable() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(ebook.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));

        assertThrows(ImmutableOrderException.class, () -> scenario.buyerPort.cancelOrder(scenario.buyerUser, pending));
        assertThrows(ImmutableOrderException.class,
                () -> scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD)));
        assertEquals(OrderStatus.DELIVERED, scenario.buyerPort.consultMyOrder(scenario.buyerUser, pending).getStatus());
    }

    @Test
    void anOrderCannotBePaidTwice() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));
        assertThrows(RuntimeException.class,
                () -> scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD)));
        assertEquals(8 + 1, stockOf("PH-1").getOnHand());
    }

    @Test
    void buyersOnlySeeTheirOwnOrders() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        var otherBuyer = scenario.registerBuyer("2000000", "Maria Lopez", "maria@nexus.com");

        assertTrue(scenario.buyerPort.consultMyOrders(otherBuyer).isEmpty());
        assertThrows(RuntimeException.class, () -> scenario.buyerPort.consultMyOrder(otherBuyer, pending));
        assertThrows(RuntimeException.class, () -> scenario.buyerPort.cancelOrder(otherBuyer, pending));
        assertThrows(RuntimeException.class,
                () -> scenario.buyerPort.payOrder(otherBuyer, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD)));
    }

    @Test
    void sellersOnlySeeTheLinesOfTheirOwnProducts() {
        Product otherProduct = scenario.publishedPhysicalProduct(scenario.otherSellerUser,
                scenario.container.warehouses.findBySeller(scenario.otherSellerUser.getSeller()).get(0), "Lamp", "80000", 10, "LP-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(otherProduct.getVariants().get(0), 2));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);

        List<Order> mine = scenario.sellerPort.consultMyOrders(scenario.sellerUser);
        assertEquals(1, mine.size());
        assertEquals(1, mine.get(0).getItems().size());
        assertEquals("Phone", mine.get(0).getItems().get(0).getProductName());
        assertEquals(1, scenario.sellerPort.consultMyOrder(scenario.sellerUser, pending).getItems().size());
        assertNull(scenario.container.orders.findById(pending).orElseThrow().getPaidAt());
        assertEquals(2, scenario.logisticPort.consultOrder(scenario.operator, pending).getItems().size());
    }

    @Test
    void sellerWithoutProductsInAnOrderCannotConsultIt() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.consultMyOrder(scenario.otherSellerUser, pending));
        assertTrue(scenario.sellerPort.consultMyOrders(scenario.otherSellerUser).isEmpty());
    }

    @Test
    void damagedStockIsNotCountedAsAvailableForNewReservationsAfterPartialReservation() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 10));
        scenario.buyerPort.checkout(scenario.buyerUser, null);
        assertEquals(InventoryStatus.AVAILABLE, stockOf("PH-1").getStatus());
        assertEquals(0, stockOf("PH-1").available());
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        assertThrows(RuntimeException.class, () -> scenario.buyerPort.checkout(scenario.buyerUser, null));
    }
}
