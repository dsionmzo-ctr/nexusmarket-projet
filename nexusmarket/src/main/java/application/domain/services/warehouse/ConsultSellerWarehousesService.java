package application.domain.services.warehouse;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.seller.ResolveAuthenticatedSellerService;
import java.util.List;

/**
 * Consults the warehouses of the authenticated seller.
 */
public class ConsultSellerWarehousesService {

    private final ResolveAuthenticatedSellerService resolveAuthenticatedSellerService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public ConsultSellerWarehousesService(ResolveAuthenticatedSellerService resolveAuthenticatedSellerService,
            WarehouseRepositoryPort warehouseRepositoryPort) {
        this.resolveAuthenticatedSellerService = resolveAuthenticatedSellerService;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public List<Warehouse> execute(User user) {
        Seller seller = resolveAuthenticatedSellerService.execute(user);
        return warehouseRepositoryPort.findBySeller(seller);
    }
}
