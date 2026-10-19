package application.domain.ports.in;

import application.domain.models.AdministrativeSummary;
import application.domain.models.AuditLog;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/**
 * Input port: read-only operations of the SUPERVISOR role (operational follow-up).
 */
public interface SupervisorPort {

    AdministrativeSummary consultSummary(User supervisor);

    List<User> consultUsers(User supervisor);

    List<Product> consultProducts(User supervisor);

    List<Warehouse> consultWarehouses(User supervisor);

    List<Inventory> consultWarehouseInventory(User supervisor, Warehouse warehouse);

    List<Order> consultOrders(User supervisor);

    Order consultOrder(User supervisor, Order order);

    List<Shipment> consultShipments(User supervisor, Shipment filter);

    List<Refund> consultRefunds(User supervisor, Refund filter);

    List<AuditLog> consultAuditLog(User supervisor, AuditLog filter);

    List<Operation> consultAllOperations(User supervisor);
}
