package application.domain.services.inventory;

import application.domain.models.Inventory;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.seller.ResolveAuthenticatedSellerService;
import java.util.List;

/**
 * Consults the inventory of the products of the authenticated seller.
 */
public class ConsultSellerInventoryService {

    private final ResolveAuthenticatedSellerService resolveAuthenticatedSellerService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ConsultSellerInventoryService(ResolveAuthenticatedSellerService resolveAuthenticatedSellerService,
            InventoryRepositoryPort inventoryRepositoryPort) {
        this.resolveAuthenticatedSellerService = resolveAuthenticatedSellerService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public List<Inventory> execute(User user) {
        Seller seller = resolveAuthenticatedSellerService.execute(user);
        return inventoryRepositoryPort.findBySeller(seller);
    }
}
