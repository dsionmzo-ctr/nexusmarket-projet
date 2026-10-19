package application.adapters.useCases;

import application.domain.models.*;
import application.domain.valueobjects.*;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.AddProductVariantService;
import application.domain.services.catalog.ConsultSellerProductsService;
import application.domain.services.catalog.DiscontinueProductService;
import application.domain.services.catalog.PublishProductService;
import application.domain.services.catalog.RegisterProductService;
import application.domain.services.catalog.SuspendProductService;
import application.domain.services.catalog.UpdateProductService;
import application.domain.services.inventory.ConsultSellerInventoryService;
import application.domain.services.inventory.RegisterInventoryInflowService;
import application.domain.services.operation.ConsultUserOperationsService;
import application.domain.services.order.ConsultSellerOrderService;
import application.domain.services.order.ConsultSellerOrdersService;
import application.domain.services.warehouse.ConsultSellerWarehousesService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case adapter of the Seller role: own catalog, own inventory inflows and orders containing own products.
 * Delegates every operation to the single-responsibility domain services; it holds no business rule.
 */
@Service
@Transactional
public class SellerUseCaseImpl implements SellerPort {

    private final RegisterProductService registerProductService;
    private final UpdateProductService updateProductService;
    private final AddProductVariantService addProductVariantService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final ConsultSellerProductsService consultSellerProductsService;
    private final ConsultSellerWarehousesService consultSellerWarehousesService;
    private final RegisterInventoryInflowService registerInventoryInflowService;
    private final ConsultSellerInventoryService consultSellerInventoryService;
    private final ConsultSellerOrdersService consultSellerOrdersService;
    private final ConsultSellerOrderService consultSellerOrderService;
    private final ConsultUserOperationsService consultUserOperationsService;

    public SellerUseCaseImpl(RegisterProductService registerProductService,
            UpdateProductService updateProductService,
            AddProductVariantService addProductVariantService,
            PublishProductService publishProductService,
            SuspendProductService suspendProductService,
            DiscontinueProductService discontinueProductService,
            ConsultSellerProductsService consultSellerProductsService,
            ConsultSellerWarehousesService consultSellerWarehousesService,
            RegisterInventoryInflowService registerInventoryInflowService,
            ConsultSellerInventoryService consultSellerInventoryService,
            ConsultSellerOrdersService consultSellerOrdersService,
            ConsultSellerOrderService consultSellerOrderService,
            ConsultUserOperationsService consultUserOperationsService) {
        this.registerProductService = registerProductService;
        this.updateProductService = updateProductService;
        this.addProductVariantService = addProductVariantService;
        this.publishProductService = publishProductService;
        this.suspendProductService = suspendProductService;
        this.discontinueProductService = discontinueProductService;
        this.consultSellerProductsService = consultSellerProductsService;
        this.consultSellerWarehousesService = consultSellerWarehousesService;
        this.registerInventoryInflowService = registerInventoryInflowService;
        this.consultSellerInventoryService = consultSellerInventoryService;
        this.consultSellerOrdersService = consultSellerOrdersService;
        this.consultSellerOrderService = consultSellerOrderService;
        this.consultUserOperationsService = consultUserOperationsService;
    }

    @Override
    public Product registerProduct(User user, Product product) {
        return registerProductService.execute(user, product);
    }

    @Override
    public Product updateProduct(User user, Product changes) {
        return updateProductService.execute(user, changes);
    }

    @Override
    public Product addProductVariant(User user, Product product, ProductVariant variant) {
        return addProductVariantService.execute(user, product, variant);
    }

    @Override
    public Product publishProduct(User user, Product product) {
        return publishProductService.execute(user, product);
    }

    @Override
    public Product suspendProduct(User user, Product product) {
        return suspendProductService.execute(user, product);
    }

    @Override
    public Product discontinueProduct(User user, Product product) {
        return discontinueProductService.execute(user, product);
    }

    @Override
    public List<Product> consultMyProducts(User user) {
        return consultSellerProductsService.execute(user);
    }

    @Override
    public List<Warehouse> consultMyWarehouses(User user) {
        return consultSellerWarehousesService.execute(user);
    }

    @Override
    public Inventory registerInventoryInflow(User user, InventoryMovement movement) {
        return registerInventoryInflowService.execute(user, movement);
    }

    @Override
    public List<Inventory> consultMyInventory(User user) {
        return consultSellerInventoryService.execute(user);
    }

    @Override
    public List<Order> consultMyOrders(User user) {
        return consultSellerOrdersService.execute(user);
    }

    @Override
    public Order consultMyOrder(User user, Order order) {
        return consultSellerOrderService.execute(user, order);
    }

    @Override
    public List<Operation> consultMyOperations(User user) {
        return consultUserOperationsService.execute(user);
    }
}
