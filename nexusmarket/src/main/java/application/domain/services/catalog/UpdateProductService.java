package application.domain.services.catalog;

import application.domain.models.Operation;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A SELLER updates the name, description and price of one of its products.
 */
public class UpdateProductService {

    private final FindSellerProductService findSellerProductService;
    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public UpdateProductService(FindSellerProductService findSellerProductService,
            ProductRepositoryPort productRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findSellerProductService = findSellerProductService;
        this.productRepositoryPort = productRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Product execute(User user, Product changes) {
        Product product = findSellerProductService.execute(user, changes);
        product.update(changes.getName(), changes.getDescription(), changes.getPrice());
        productRepositoryPort.update(product);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.PRODUCT_UPDATE, user, product, LocalDateTime.now(clock)),
                AuditDetails.of("name", product.getName()));
        return product;
    }
}
