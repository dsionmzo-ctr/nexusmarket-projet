package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.authorization.AuthorizeWarehouseAccessService;
import application.domain.services.warehouse.FindWarehouseService;
import java.util.List;

/**
 * Consults the inventory stored in a warehouse (LOGISTIC_OPERATOR, ADMIN and SUPERVISOR; a SELLER only in
 * its own warehouses).
 */
public class ConsultWarehouseInventoryService {

    private final FindWarehouseService findWarehouseService;
    private final AuthorizeWarehouseAccessService authorizeWarehouseAccessService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ConsultWarehouseInventoryService(FindWarehouseService findWarehouseService,
            AuthorizeWarehouseAccessService authorizeWarehouseAccessService,
            InventoryRepositoryPort inventoryRepositoryPort) {
        this.findWarehouseService = findWarehouseService;
        this.authorizeWarehouseAccessService = authorizeWarehouseAccessService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public List<Inventory> execute(User user, Warehouse probe) {
        Warehouse warehouse = findWarehouseService.execute(probe);
        authorizeWarehouseAccessService.execute(user, warehouse);
        return inventoryRepositoryPort.findByWarehouse(warehouse);
    }
}
