package application.domain;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.AdministrativeSummary;
import application.domain.models.AuditLog;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.support.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Responsibility matrix (section 12), RG-01..RG-03, administrative reports and the operation/audit trail. */
class AuthorizationAndAuditTest {

    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenario = new Scenario();
    }

    @Test
    void everyRoleIsRestrictedToItsOwnOperations() {
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.consultUsers(scenario.buyerUser));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.consultUsers(scenario.sellerUser));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.buyerPort.consultMyCart(scenario.sellerUser));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.sellerPort.consultMyProducts(scenario.buyerUser));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.logisticPort.consultReturnsToReceive(scenario.sellerUser));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.supervisorPort.consultUsers(scenario.buyerUser));
        assertEquals(6, scenario.adminPort.consultUsers(scenario.admin).size());
    }

    @Test
    void sellersCannotSelfRegisterAndBuyersCannotIncorporateThem() {
        var seller = new application.domain.models.Seller();
        seller.setIdentification("999");
        seller.setName("Self Registered");
        seller.setEmail("self@nexus.com");
        seller.setBusinessName("Self");
        var warehouse = Scenario.warehouse("W", "SELLER");
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.registerSeller(scenario.buyerUser, seller, warehouse));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.registerSeller(scenario.sellerUser, seller, warehouse));
    }

    @Test
    void inactiveUsersCannotOperate() {
        scenario.adminPort.changeUserStatus(scenario.admin, scenario.operator, UserStatus.INACTIVE);
        User stored = scenario.container.users.findById(scenario.operator).orElseThrow();
        assertThrows(UnauthorizedOperationException.class, () -> scenario.logisticPort.consultWarehouses(stored));
    }

    @Test
    void incorporatingASellerCreatesItsFirstWarehouseOfTypeSeller() {
        assertEquals(1, scenario.sellerPort.consultMyWarehouses(scenario.sellerUser).size());
        assertEquals("SELLER", scenario.sellerWarehouse.getType().getCode());
        assertEquals(3, scenario.adminPort.consultWarehouses(scenario.admin).size());
        assertTrue(scenario.sellerPort.consultMyWarehouses(scenario.otherSellerUser).stream()
                .noneMatch(w -> w.getId().equals(scenario.sellerWarehouse.getId())));
    }

    @Test
    void inactiveWarehousesRejectInflows() {
        scenario.adminPort.changeWarehouseStatus(scenario.admin, scenario.sellerWarehouse, WarehouseStatus.INACTIVE);
        Product product = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", application.domain.valueobjects.ProductType.PHYSICAL, "1000", "PH-1"));
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(product.getVariants().get(0), scenario.sellerWarehouse, 1, "x")));
    }

    @Test
    void supervisorHasReadAccessEverywhereButNoWriteAccess() {
        scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        assertEquals(1, scenario.supervisorPort.consultProducts(scenario.supervisor).size());
        assertEquals(6, scenario.supervisorPort.consultUsers(scenario.supervisor).size());
        assertEquals(1, scenario.supervisorPort.consultWarehouseInventory(scenario.supervisor, scenario.sellerWarehouse).size());
        assertThrows(UnauthorizedOperationException.class,
                () -> scenario.sellerPort.registerProduct(scenario.supervisor, Scenario.product("X", application.domain.valueobjects.ProductType.PHYSICAL, "1", "X-1")));
        assertThrows(UnauthorizedOperationException.class,
                () -> scenario.adminPort.changeUserStatus(scenario.supervisor, scenario.buyerUser, UserStatus.BLOCKED));
    }

    @Test
    void administrativeSummaryConsolidatesTheMarketplace() {
        Product phone = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));

        AdministrativeSummary summary = scenario.supervisorPort.consultSummary(scenario.supervisor);
        assertEquals(6, summary.getTotalUsers());
        assertEquals(1L, summary.getUsersByRole().get(SystemRole.BUYER.getCode()).longValue());
        assertEquals(2L, summary.getUsersByRole().get(SystemRole.SELLER.getCode()).longValue());
        assertEquals(1L, summary.getOrdersByStatus().get(OrderStatus.PAID.getCode()).longValue());
        assertEquals(1L, summary.getPublishedProducts());
        assertEquals(1L, summary.getPendingShipments());
        assertEquals(0L, summary.getPendingRefunds());
        // Sales are the order subtotals (the 19% tax belongs to the invoice, not to the sale).
        assertEquals(new java.math.BigDecimal("1000000.00"), summary.getPaidSales().amount());
        assertEquals(1L, scenario.adminPort.consultSummary(scenario.admin).getPublishedProducts());
    }

    @Test
    void everyBusinessActionLeavesAnOperationAndAnAuditLog() {
        Product phone = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(phone.getVariants().get(0), 1));
        Order pending = scenario.buyerPort.checkout(scenario.buyerUser, null);
        scenario.buyerPort.payOrder(scenario.buyerUser, Scenario.payment(pending.getId(), PaymentMethod.CREDIT_CARD));

        List<Operation> buyerOperations = scenario.buyerPort.consultMyOperations(scenario.buyerUser);
        List<OperationType> types = buyerOperations.stream().map(Operation::getOperationType).toList();
        assertTrue(types.contains(OperationType.BUYER_REGISTRATION));
        assertTrue(types.contains(OperationType.ORDER_CREATION));
        assertTrue(types.contains(OperationType.ORDER_PAYMENT));
        assertTrue(types.contains(OperationType.INVOICE_ISSUANCE));
        assertEquals(scenario.container.operations.findAll().size(), scenario.container.auditLogs.findAll().size());

        List<AuditLog> logs = scenario.adminPort.consultAuditLog(scenario.admin, null);
        assertTrue(logs.size() >= 10);
        assertTrue(logs.stream().allMatch(l -> l.getAuditId() != null && l.getPerformedBy() != null && l.getUserRole() != null));

        AuditLog filter = new AuditLog();
        filter.setOperationType(OperationType.ORDER_PAYMENT);
        List<AuditLog> payments = scenario.supervisorPort.consultAuditLog(scenario.supervisor, filter);
        assertEquals(1, payments.size());
        assertEquals(SystemRole.BUYER, payments.get(0).getUserRole());
        assertNotNull(payments.get(0).getDetails().get("paymentMethod"));

        AuditLog byUser = new AuditLog();
        byUser.setPerformedBy(scenario.sellerUser);
        assertTrue(scenario.adminPort.consultAuditLog(scenario.admin, byUser).stream()
                .allMatch(l -> l.getPerformedBy().getId().equals(scenario.sellerUser.getId())));

        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.consultAuditLog(scenario.buyerUser, null));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.adminPort.consultAllOperations(scenario.sellerUser));
        assertTrue(scenario.adminPort.consultAllOperations(scenario.admin).size() > buyerOperations.size());
    }

    @Test
    void auditDetailsNeverContainPasswords() {
        scenario.container.auditLogs.findAll().forEach(log -> log.getDetails().forEach((key, value) -> {
            assertTrue(!key.toLowerCase().contains("password"));
            assertTrue(value == null || !String.valueOf(value).contains(Scenario.PASSWORD));
        }));
    }
}
