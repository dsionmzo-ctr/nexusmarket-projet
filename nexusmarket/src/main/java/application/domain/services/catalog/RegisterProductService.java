package application.domain.services.catalog;

import application.domain.exceptions.DuplicateResourceException;
import application.domain.models.Operation;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.operation.AuditDetails;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.seller.ResolveAuthenticatedSellerService;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ProductStatus;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * A SELLER registers one of its products with its variants. The product is created SUSPENDED (not visible)
 * and the SKU of every variant must be unique in the catalog.
 */
public class RegisterProductService {

    private final ResolveAuthenticatedSellerService resolveAuthenticatedSellerService;
    private final ProductRepositoryPort productRepositoryPort;
    private final RegisterOperationAndAuditService registerOperationAndAuditService;
    private final Clock clock;

    public RegisterProductService(ResolveAuthenticatedSellerService resolveAuthenticatedSellerService,
            ProductRepositoryPort productRepositoryPort,
            RegisterOperationAndAuditService registerOperationAndAuditService,
            Clock clock) {
        this.resolveAuthenticatedSellerService = resolveAuthenticatedSellerService;
        this.productRepositoryPort = productRepositoryPort;
        this.registerOperationAndAuditService = registerOperationAndAuditService;
        this.clock = clock;
    }

    public Product execute(User user, Product product) {
        Seller seller = resolveAuthenticatedSellerService.execute(user);
        product.setSeller(seller);
        product.setStatus(ProductStatus.SUSPENDED);
        product.ensureValid();
        for (ProductVariant variant : product.getVariants()) {
            if (productRepositoryPort.existsVariantSku(variant)) {
                throw new DuplicateResourceException("Variant SKU already exists: " + variant.getSku());
            }
        }
        Product saved = productRepositoryPort.save(product);
        registerOperationAndAuditService.execute(
                Operation.of(OperationType.PRODUCT_REGISTRATION, user, saved, LocalDateTime.now(clock)),
                AuditDetails.of("name", saved.getName(), "type", saved.getType().getCode(),
                        "variants", saved.getVariants().size()));
        return saved;
    }
}
