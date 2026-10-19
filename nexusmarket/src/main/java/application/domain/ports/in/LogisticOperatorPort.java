package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.ProductReturn;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/**
 * Input port: operations of the LOGISTIC_OPERATOR role (physical warehouse and dispatch operation).
 */
public interface LogisticOperatorPort {

    List<Warehouse> consultWarehouses(User user);

    List<Inventory> consultWarehouseInventory(User user, Warehouse warehouse);

    List<InventoryMovement> consultInventoryMovements(User user, Inventory inventory);

    /** {@code movement} carries the inventory (variant + warehouse), the received quantity and a note. */
    Inventory registerInventoryInflow(User user, InventoryMovement movement);

    /** {@code movement.quantity} is the new physical stock. */
    Inventory adjustInventory(User user, InventoryMovement movement);

    Inventory markInventoryDamaged(User user, InventoryMovement movement);

    Inventory markInventoryAvailable(User user, InventoryMovement movement);

    List<Order> consultOrders(User user);

    Order consultOrder(User user, Order order);

    /** {@code filter.status} may be null to list every shipment. */
    List<Shipment> consultShipments(User user, Shipment filter);

    Shipment dispatchShipment(User user, Shipment shipment);

    Shipment confirmDelivery(User user, Shipment shipment);

    List<ProductReturn> consultReturnsToReceive(User user);

    /** {@code productReturn} carries the return id and the receiving warehouse. */
    ProductReturn receiveReturn(User user, ProductReturn productReturn);

    /** Rejects a return (goods not accepted); the refund opened with it is rejected too. */
    ProductReturn rejectReturn(User user, ProductReturn productReturn);

    List<Operation> consultMyOperations(User user);
}
