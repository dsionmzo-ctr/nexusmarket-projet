package application.domain;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.MovementType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.support.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Catalog and distributed inventory domains: product lifecycle, ownership, stock rules (section 11). */
class CatalogAndInventoryTest {

    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenario = new Scenario();
    }

    @Test
    void registeredProductsStartSuspendedAndAreInvisibleInThePublicCatalog() {
        Product registered = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        assertEquals(ProductStatus.SUSPENDED, registered.getStatus());
        assertTrue(scenario.publicAccess.consultPublishedProducts().isEmpty());

        Product published = scenario.sellerPort.publishProduct(scenario.sellerUser, registered);
        assertEquals(ProductStatus.PUBLISHED, published.getStatus());
        assertEquals(1, scenario.publicAccess.consultPublishedProducts().size());
        assertEquals("Phone", scenario.publicAccess.consultPublishedProduct(registered).getName());
    }

    @Test
    void discontinuedProductsLeaveTheCatalogAndCannotBeEdited() {
        Product product = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        scenario.sellerPort.discontinueProduct(scenario.sellerUser, product);
        assertTrue(scenario.publicAccess.consultPublishedProducts().isEmpty());
        Product changes = new Product();
        changes.setId(product.getId());
        changes.setName("Renamed");
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.updateProduct(scenario.sellerUser, changes));
    }

    @Test
    void variantSkusAreUnique() {
        scenario.sellerPort.registerProduct(scenario.sellerUser, Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        assertThrows(DuplicateResourceException.class, () -> scenario.sellerPort.registerProduct(scenario.otherSellerUser,
                Scenario.product("Other phone", ProductType.PHYSICAL, "900000", "PH-1")));
    }

    @Test
    void aSellerCannotManageAnotherSellersProducts() {
        Product product = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.publishProduct(scenario.otherSellerUser, product));
        assertTrue(scenario.sellerPort.consultMyProducts(scenario.otherSellerUser).isEmpty());
        assertEquals(1, scenario.sellerPort.consultMyProducts(scenario.sellerUser).size());
    }

    @Test
    void onlySellersRegisterProducts() {
        assertThrows(UnauthorizedOperationException.class, () -> scenario.sellerPort.registerProduct(scenario.buyerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1")));
    }

    @Test
    void inflowCreatesInventoryAndMovementHistory() {
        Product product = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        ProductVariant variant = product.getVariants().get(0);

        Inventory inventory = scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(variant, scenario.sellerWarehouse, 10, "first batch"));
        assertEquals(10, inventory.getOnHand());
        assertEquals(0, inventory.getReserved());
        assertEquals(10, inventory.available());

        scenario.sellerPort.registerInventoryInflow(scenario.sellerUser, Scenario.inflow(variant, scenario.sellerWarehouse, 5, "second"));
        List<InventoryMovement> history = scenario.logisticPort.consultInventoryMovements(scenario.operator, inventory);
        assertEquals(2, history.size());
        assertTrue(history.stream().allMatch(m -> MovementType.INFLOW.equals(m.getType())));
        assertEquals(15, scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0).getOnHand());
    }

    @Test
    void aSellerCannotStockAnotherSellersWarehouse() {
        Product product = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.otherSellerUser,
                Scenario.inflow(product.getVariants().get(0), scenario.sellerWarehouse, 10, "not mine")));
    }

    @Test
    void digitalProductsDoNotUseInventory() {
        Product digital = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("E-book", ProductType.DIGITAL, "50000", "EB-1"));
        assertThrows(InvalidInventoryException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(digital.getVariants().get(0), scenario.sellerWarehouse, 10, "invalid")));
    }

    @Test
    void inflowsRequireAPositiveQuantityAndAnExistingWarehouse() {
        Product product = scenario.sellerPort.registerProduct(scenario.sellerUser,
                Scenario.product("Phone", ProductType.PHYSICAL, "1000000", "PH-1"));
        ProductVariant variant = product.getVariants().get(0);
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(variant, scenario.sellerWarehouse, 0, "zero")));
        assertThrows(RuntimeException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(variant, scenario.sellerWarehouse, -3, "negative")));
        scenario.sellerWarehouse.setId(9999L);
        assertThrows(EntityNotFoundException.class, () -> scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(variant, scenario.sellerWarehouse, 3, "missing warehouse")));
    }

    @Test
    void stockCanNeverBecomeNegative() {
        Product product = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        Inventory inventory = scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0);

        InventoryMovement negative = Scenario.inflow(product.getVariants().get(0), scenario.sellerWarehouse, -1, "invalid");
        negative.getInventory().setId(inventory.getId());
        assertThrows(RuntimeException.class, () -> scenario.logisticPort.adjustInventory(scenario.operator, negative));
        assertEquals(5, scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0).getOnHand());
    }

    @Test
    void cannotReserveMoreThanTheAvailableStock() {
        Product product = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 3, "PH-1");
        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(product.getVariants().get(0), 4));
        assertThrows(InsufficientStockException.class, () -> scenario.buyerPort.checkout(scenario.buyerUser, null));
        assertEquals(0, scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0).getReserved());
        assertTrue(scenario.buyerPort.consultMyOrders(scenario.buyerUser).isEmpty());
    }

    @Test
    void damagedInventoryCannotBeReservedUntilItIsRestored() {
        Product product = scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        Inventory inventory = scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0);
        InventoryMovement note = new InventoryMovement();
        note.setInventory(inventory);
        note.setNote("water damage");

        Inventory damaged = scenario.logisticPort.markInventoryDamaged(scenario.operator, note);
        assertEquals(InventoryStatus.DAMAGED, damaged.getStatus());

        scenario.buyerPort.addItemToCart(scenario.buyerUser, Scenario.cartItem(product.getVariants().get(0), 1));
        assertThrows(RuntimeException.class, () -> scenario.buyerPort.checkout(scenario.buyerUser, null));

        scenario.logisticPort.markInventoryAvailable(scenario.operator, note);
        assertEquals(1, scenario.buyerPort.checkout(scenario.buyerUser, null).getItems().size());
    }

    @Test
    void onlyLogisticOperatorsAdjustOrMarkInventory() {
        scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        Inventory inventory = scenario.logisticPort.consultWarehouseInventory(scenario.operator, scenario.sellerWarehouse).get(0);
        InventoryMovement adjustment = new InventoryMovement();
        adjustment.setInventory(inventory);
        adjustment.setQuantity(1);
        adjustment.setNote("recount");
        assertThrows(UnauthorizedOperationException.class, () -> scenario.logisticPort.adjustInventory(scenario.sellerUser, adjustment));
        assertThrows(UnauthorizedOperationException.class, () -> scenario.logisticPort.markInventoryDamaged(scenario.buyerUser, adjustment));
    }

    @Test
    void sellersOnlySeeTheStockOfTheirOwnProducts() {
        scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        assertEquals(1, scenario.sellerPort.consultMyInventory(scenario.sellerUser).size());
        assertTrue(scenario.sellerPort.consultMyInventory(scenario.otherSellerUser).isEmpty());
    }

    @Test
    void sellerCannotCreateAnotherInventoryRecordForTheSameVariantAndWarehouse() {
        scenario.publishedPhysicalProduct(scenario.sellerUser, scenario.sellerWarehouse, "Phone", "1000000", 5, "PH-1");
        assertEquals(1, scenario.container.inventories.findAll().size());
        Product product = scenario.sellerPort.consultMyProducts(scenario.sellerUser).get(0);
        scenario.sellerPort.registerInventoryInflow(scenario.sellerUser,
                Scenario.inflow(product.getVariants().get(0), scenario.sellerWarehouse, 2, "again"));
        assertEquals(1, scenario.container.inventories.findAll().size());
        assertEquals(7, scenario.container.inventories.findAll().get(0).getOnHand());
    }
}
