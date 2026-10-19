package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.seller.ResolveAuthenticatedSellerService;
import java.util.List;

/**
 * Consults the products of the authenticated seller (any status).
 */
public class ConsultSellerProductsService {

    private final ResolveAuthenticatedSellerService resolveAuthenticatedSellerService;
    private final ProductRepositoryPort productRepositoryPort;

    public ConsultSellerProductsService(ResolveAuthenticatedSellerService resolveAuthenticatedSellerService,
            ProductRepositoryPort productRepositoryPort) {
        this.resolveAuthenticatedSellerService = resolveAuthenticatedSellerService;
        this.productRepositoryPort = productRepositoryPort;
    }

    public List<Product> execute(User user) {
        Seller seller = resolveAuthenticatedSellerService.execute(user);
        return productRepositoryPort.findBySeller(seller);
    }
}
