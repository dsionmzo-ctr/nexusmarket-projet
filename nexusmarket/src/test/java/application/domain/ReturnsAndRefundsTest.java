package application.domain;

import application.domain.exceptions.InvalidReturnException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.ProductReturn;
import application.domain.models.ProductVariant;
import application.domain.models.Refund;
import application.domain.models.ReturnRequestResult;
import application.domain.models.Shipment;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.RefundStatus;
import application.domain.exceptions.DomainException;
import application.domain.valueobjects.ReturnReason;
import application.domain.valueobjects.ReturnStatus;
import application.support.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Returns and refunds domain: window, quantities, reception, restock and administrative decision. */
class ReturnsAndRefundsTest {

    private Scenario scenario;
    private Product phone;
    private Order delivered;

    @BeforeEach
    void setUp() {
        scenario = new Scenario();
        phone = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 10, "PH-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 3));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));
        Shipment shipment = scenario.buyerPort.consultMyShipment(scenario.buyerUser, pending);
        scenario.logisticPort.dispatchShipment(scenario.operator, shipment);
        scenario.logisticPort.confirmDelivery(scenario.operator, shipment);
        delivered = scenario.buyerPort.consultMyOrder(scenario.buyerUser, pending);
        assertEquals(application.domain.valueobjects.OrderStatus.DELIVERED, delivered.getStatus());
    }

    private ProductReturn returnRequest(Order order, ProductVariant variant, int quantity) {
        ProductReturn request = new ProductReturn();
        request.setOrder(Scenario.orderRef(order.getId()));
        OrderItem item = new OrderItem();
        ProductVariant ref = new ProductVariant();
        ref.setId(variant.getId());
        item.setVariant(ref);
        request.setOrderItem(item);
        request.setQuantity(quantity);
        request.setReason(ReturnReason.DEFECTIVE);
        request.setComment("does not turn on");
        return request;
    }

    private Inventory stock() {
        return scenario.container.inventories.findAll().get(0);
    }

    @Test
    void returnRequestCreatesAPendingReturnAndARefundForTheReturnedValue() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 2));

        assertEquals(ReturnStatus.REQUESTED, result.getProductReturn().getStatus());
        assertEquals(RefundStatus.REQUESTED, result.getRefund().getStatus());
        assertEquals(new BigDecimal("2000000.00"), result.getRefund().getAmount().amount());
        assertEquals(1, scenario.buyerPort.consultMyReturns(scenario.buyerUser).size());
        assertEquals(1, scenario.buyerPort.consultMyRefunds(scenario.buyerUser).size());
        assertEquals(1, scenario.adminPort.consultPendingRefunds(scenario.admin).size());
    }

    @Test
    void quantityCannotExceedWhatWasPurchasedAcrossAllActiveReturns() {
        scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 2));
        assertThrows(InvalidReturnException.class,
                () -> scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 2)));
        scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1));
    }

    @Test
    void returnWindowIsEnforced() {
        scenario.container.clock.advance(Duration.ofDays(31));
        assertThrows(InvalidReturnException.class,
                () -> scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1)));
    }

    @Test
    void onlyDeliveredOrdersCanBeReturned() {
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        assertThrows(InvalidReturnException.class,
                () -> scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(pending, phone.getVariants().get(0), 1)));
    }

    @Test
    void digitalProductsCannotBeReturned() {
        Product ebook = scenario.publishedDigitalProduct(scenario.sellerUser, "E-book", "50000", "EB-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(ebook.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));
        assertThrows(InvalidReturnException.class,
                () -> scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(pending, ebook.getVariants().get(0), 1)));
    }

    @Test
    void buyersCannotReturnOrdersOfOtherBuyers() {
        var other = scenario.registerBuyer("2000000", "Maria Lopez", "maria@nexus.com");
        assertThrows(RuntimeException.class,
                () -> scenario.buyerPort.requestReturn(other, returnRequest(delivered, phone.getVariants().get(0), 1)));
    }

    @Test
    void receivingAReturnRestocksTheWarehouseAndRegistersTheMovement() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 2));
        assertEquals(7, stock().getOnHand());

        ProductReturn reception = new ProductReturn();
        reception.setId(result.getProductReturn().getId());
        reception.setReceivedWarehouse(scenario.sellerWarehouse);
        ProductReturn received = scenario.logisticPort.receiveReturn(scenario.operator, reception);

        assertEquals(ReturnStatus.RECEIVED, received.getStatus());
        assertNotNull(received.getReceivedAt());
        assertEquals(9, stock().getOnHand());
        assertTrue(scenario.logisticPort.consultReturnsToReceive(scenario.operator).isEmpty());
        assertThrows(RuntimeException.class, () -> scenario.logisticPort.receiveReturn(scenario.operator, reception));
    }

    @Test
    void rejectedReturnsFreeTheirQuantityAndAreNotReceived() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 3));
        ProductReturn probe = new ProductReturn();
        probe.setId(result.getProductReturn().getId());
        assertEquals(ReturnStatus.REJECTED, scenario.logisticPort.rejectReturn(scenario.operator, probe).getStatus());
        assertEquals(7, stock().getOnHand());
        assertEquals(RefundStatus.REJECTED, scenario.buyerPort.consultMyRefunds(scenario.buyerUser).get(0).getStatus());
        scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 3));
    }

    private void receive(ProductReturn productReturn) {
        ProductReturn reception = new ProductReturn();
        reception.setId(productReturn.getId());
        reception.setReceivedWarehouse(scenario.sellerWarehouse);
        scenario.logisticPort.receiveReturn(scenario.operator, reception);
    }

    @Test
    void refundCannotBeApprovedBeforeTheGoodsAreReceived() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1));
        Refund decision = new Refund();
        decision.setId(result.getRefund().getId());
        assertThrows(DomainException.class, () -> scenario.adminPort.approveRefund(scenario.admin, decision));
    }

    @Test
    void adminApprovesARefundOnce() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1));
        receive(result.getProductReturn());
        Refund decision = new Refund();
        decision.setId(result.getRefund().getId());
        decision.setDecisionNote("Verified defect");

        Refund approved = scenario.adminPort.approveRefund(scenario.admin, decision);

        assertEquals(RefundStatus.APPROVED, approved.getStatus());
        assertEquals(scenario.admin.getId(), approved.getDecidedBy().getId());
        assertEquals("Verified defect", approved.getDecisionNote());
        assertTrue(scenario.adminPort.consultPendingRefunds(scenario.admin).isEmpty());
        assertThrows(RuntimeException.class, () -> scenario.adminPort.approveRefund(scenario.admin, decision));
        assertThrows(RuntimeException.class, () -> scenario.adminPort.rejectRefund(scenario.admin, decision));
    }

    @Test
    void adminRejectsARefund() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1));
        Refund decision = new Refund();
        decision.setDecisionNote("Not covered");
        decision.setId(result.getRefund().getId());
        assertEquals(RefundStatus.REJECTED, scenario.adminPort.rejectRefund(scenario.admin, decision).getStatus());
        assertTrue(scenario.adminPort.consultPendingRefunds(scenario.admin).isEmpty());
    }

    @Test
    void refundDecisionsAreAdminOnlyAndSupervisorsOnlyRead() {
        ReturnRequestResult result = scenario.buyerPort.requestReturn(scenario.buyerUser, returnRequest(delivered, phone.getVariants().get(0), 1));
        Refund decision = new Refund();
        decision.setId(result.getRefund().getId());
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.approveRefund(scenario.operator, decision));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.approveRefund(scenario.supervisor, decision));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.approveRefund(scenario.buyerUser, decision));

        Refund filter = new Refund();
        filter.setStatus(RefundStatus.REQUESTED);
        List<Refund> visible = scenario.supervisorPort.consultRefunds(scenario.supervisor, filter);
        assertEquals(1, visible.size());
    }
}
