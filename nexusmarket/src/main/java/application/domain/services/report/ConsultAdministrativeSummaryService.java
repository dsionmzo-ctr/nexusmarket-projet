package application.domain.services.report;

import application.domain.models.AdministrativeSummary;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.InventoryStatus;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.SystemRole;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds the read-only consolidated figures for administrative consultation (ADMIN and SUPERVISOR).
 */
public class ConsultAdministrativeSummaryService {

    private final AuthorizeOperationService authorizeOperationService;
    private final UserRepositoryPort userRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final RefundRepositoryPort refundRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ConsultAdministrativeSummaryService(AuthorizeOperationService authorizeOperationService,
            UserRepositoryPort userRepositoryPort,
            OrderRepositoryPort orderRepositoryPort,
            ProductRepositoryPort productRepositoryPort,
            ShipmentRepositoryPort shipmentRepositoryPort,
            RefundRepositoryPort refundRepositoryPort,
            InventoryRepositoryPort inventoryRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.userRepositoryPort = userRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.refundRepositoryPort = refundRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public AdministrativeSummary execute(User user) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        AdministrativeSummary summary = new AdministrativeSummary();

        List<User> users = userRepositoryPort.findAll();
        summary.setTotalUsers(users.size());
        users.stream().collect(Collectors.groupingBy(u -> u.getRole().getCode(), Collectors.counting()))
                .forEach((role, count) -> summary.getUsersByRole().put(role, count));

        List<Order> orders = orderRepositoryPort.findAll();
        orders.stream().collect(Collectors.groupingBy(o -> o.getStatus().getCode(), Collectors.counting()))
                .forEach((status, count) -> summary.getOrdersByStatus().put(status, count));
        Money sales = null;
        for (Order order : orders) {
            boolean counted = OrderStatus.PAID.equals(order.getStatus()) || OrderStatus.SHIPPED.equals(order.getStatus())
                    || OrderStatus.DELIVERED.equals(order.getStatus());
            if (counted) {
                Money total = order.total();
                sales = sales == null ? total : (sales.hasSameCurrency(total) ? sales.add(total) : sales);
            }
        }
        summary.setPaidSales(sales);

        Product published = new Product();
        published.setStatus(ProductStatus.PUBLISHED);
        summary.setPublishedProducts(productRepositoryPort.findByStatus(published).size());
        Shipment preparing = new Shipment();
        preparing.setStatus(ShipmentStatus.PREPARING);
        summary.setPendingShipments(shipmentRepositoryPort.findByStatus(preparing).size());
        Refund requested = new Refund();
        requested.setStatus(RefundStatus.REQUESTED);
        summary.setPendingRefunds(refundRepositoryPort.findByStatus(requested).size());
        summary.setDamagedInventoryRecords(inventoryRepositoryPort.findAll().stream()
                .filter(i -> InventoryStatus.DAMAGED.equals(i.getStatus())).count());
        return summary;
    }
}
