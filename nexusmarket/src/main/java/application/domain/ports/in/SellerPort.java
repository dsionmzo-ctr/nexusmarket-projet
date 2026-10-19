package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/**
 * Input port: operations of the SELLER role. A seller only handles his own products, warehouses,
 * inventory and sales.
 */
public interface SellerPort {

    Product registerProduct(User user, Product product);

    Product updateProduct(User user, Product changes);

    Product addProductVariant(User user, Product product, ProductVariant variant);

    Product publishProduct(User user, Product product);

    Product suspendProduct(User user, Product product);

    Product discontinueProduct(User user, Product product);

    List<Product> consultMyProducts(User user);

    List<Warehouse> consultMyWarehouses(User user);

    /** {@code movement} carries the inventory (variant + warehouse), the received quantity and a note. */
    Inventory registerInventoryInflow(User user, InventoryMovement movement);

    List<Inventory> consultMyInventory(User user);

    List<Order> consultMyOrders(User user);

    Order consultMyOrder(User user, Order order);

    List<Operation> consultMyOperations(User user);
}
