package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import java.util.List;

/**
 * Public catalog: consults the PUBLISHED products (no authentication required).
 */
public class ConsultPublishedProductsService {

    private final ProductRepositoryPort productRepositoryPort;

    public ConsultPublishedProductsService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public List<Product> execute() {
        Product criteria = new Product();
        criteria.setStatus(ProductStatus.PUBLISHED);
        return productRepositoryPort.findByStatus(criteria);
    }
}
