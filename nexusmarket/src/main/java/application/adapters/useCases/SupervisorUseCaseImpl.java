package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.catalog.ConsultAllProductsService;
import application.domain.services.inventory.ConsultWarehouseInventoryService;
import application.domain.services.operation.ConsultAllOperationsService;
import application.domain.services.operation.ConsultAuditLogsService;
import application.domain.services.order.ConsultAllOrdersService;
import application.domain.services.order.ConsultOrderService;
import application.domain.services.report.ConsultAdministrativeSummaryService;
import application.domain.services.returns.ConsultRefundsService;
import application.domain.services.shipping.ConsultShipmentsService;
import application.domain.services.user.ConsultUsersService;
import application.domain.services.warehouse.ConsultWarehousesService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the Supervisor role: read-only operational follow-up.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ConsultAdministrativeSummaryService consultAdministrativeSummaryService;
    private final ConsultUsersService consultUsersService;
    private final ConsultAllProductsService consultAllProductsService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultWarehouseInventoryService consultWarehouseInventoryService;
    private final ConsultAllOrdersService consultAllOrdersService;
    private final ConsultOrderService consultOrderService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultRefundsService consultRefundsService;
    private final ConsultAuditLogsService consultAuditLogsService;
    private final ConsultAllOperationsService consultAllOperationsService;

    public SupervisorUseCaseImpl(ConsultAdministrativeSummaryService consultAdministrativeSummaryService,
            ConsultUsersService consultUsersService,
            ConsultAllProductsService consultAllProductsService,
            ConsultWarehousesService consultWarehousesService,
            ConsultWarehouseInventoryService consultWarehouseInventoryService,
            ConsultAllOrdersService consultAllOrdersService,
            ConsultOrderService consultOrderService,
            ConsultShipmentsService consultShipmentsService,
            ConsultRefundsService consultRefundsService,
            ConsultAuditLogsService consultAuditLogsService,
            ConsultAllOperationsService consultAllOperationsService) {
        this.consultAdministrativeSummaryService = consultAdministrativeSummaryService;
        this.consultUsersService = consultUsersService;
        this.consultAllProductsService = consultAllProductsService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultWarehouseInventoryService = consultWarehouseInventoryService;
        this.consultAllOrdersService = consultAllOrdersService;
        this.consultOrderService = consultOrderService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultRefundsService = consultRefundsService;
        this.consultAuditLogsService = consultAuditLogsService;
        this.consultAllOperationsService = consultAllOperationsService;
    }

    @Override
    public AdministrativeSummary consultSummary(User supervisor) {
        return consultAdministrativeSummaryService.execute(supervisor);
    }

    @Override
    public List<User> consultUsers(User supervisor) {
        return consultUsersService.execute(supervisor);
    }

    @Override
    public List<Product> consultProducts(User supervisor) {
        return consultAllProductsService.execute(supervisor);
    }

    @Override
    public List<Warehouse> consultWarehouses(User supervisor) {
        return consultWarehousesService.execute(supervisor);
    }

    @Override
    public List<Inventory> consultWarehouseInventory(User supervisor, Warehouse warehouse) {
        return consultWarehouseInventoryService.execute(supervisor, warehouse);
    }

    @Override
    public List<Order> consultOrders(User supervisor) {
        return consultAllOrdersService.execute(supervisor);
    }

    @Override
    public Order consultOrder(User supervisor, Order order) {
        return consultOrderService.execute(supervisor, order);
    }

    @Override
    public List<Shipment> consultShipments(User supervisor, Shipment filter) {
        return consultShipmentsService.execute(supervisor, filter);
    }

    @Override
    public List<Refund> consultRefunds(User supervisor, Refund filter) {
        return consultRefundsService.execute(supervisor, filter);
    }

    @Override
    public List<AuditLog> consultAuditLog(User supervisor, AuditLog filter) {
        return consultAuditLogsService.execute(supervisor, filter);
    }

    @Override
    public List<Operation> consultAllOperations(User supervisor) {
        return consultAllOperationsService.execute(supervisor);
    }
}
