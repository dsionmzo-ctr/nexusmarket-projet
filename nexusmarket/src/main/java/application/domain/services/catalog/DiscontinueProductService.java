package application.domain.services.catalog;

import application.domain.models.Operation;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ProductStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A SELLER discontinues permanently one of its products.
 */
public class DiscontinueProductService {

    private final FindSellerProductService findSellerProductService;
    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public DiscontinueProductService(FindSellerProductService findSellerProductService,
            ProductRepositoryPort productRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findSellerProductService = findSellerProductService;
        this.productRepositoryPort = productRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Product execute(User user, Product probe) {
        Product product = findSellerProductService.execute(user, probe);
        ProductStatus previous = product.getStatus();
        product.discontinue();
        productRepositoryPort.update(product);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.PRODUCT_DISCONTINUATION, user, product, LocalDateTime.now(clock)),
                AuditDetails.of("previousStatus", previous.getCode(), "newStatus", product.getStatus().getCode()));
        return product;
    }
}
