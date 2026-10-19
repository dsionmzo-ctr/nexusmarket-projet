package application.domain.services.catalog;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.models.Operation;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.OperationType;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A SELLER adds a variant (color, size, model...) to one of its products. The SKU must be unique.
 */
public class AddProductVariantService {

    private final FindSellerProductService findSellerProductService;
    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public AddProductVariantService(FindSellerProductService findSellerProductService,
            ProductRepositoryPort productRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.findSellerProductService = findSellerProductService;
        this.productRepositoryPort = productRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Product execute(User user, Product probe, ProductVariant variant) {
        Product product = findSellerProductService.execute(user, probe);
        if (variant != null && productRepositoryPort.existsVariantSku(variant)) {
            throw new DuplicateResourceException("Variant SKU already exists: " + variant.getSku());
        }
        product.addVariant(variant);
        productRepositoryPort.update(product);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.PRODUCT_VARIANT_ADDITION, user, product, LocalDateTime.now(clock)),
                AuditDetails.of("sku", variant.getSku()));
        return product;
    }
}
