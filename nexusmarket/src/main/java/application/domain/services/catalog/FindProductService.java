package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidProductException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Loads a product by its identifier or fails with EntityNotFoundException.
 */
public class FindProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public FindProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product execute(Product probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidProductException("Product identifier must be provided.");
        }
        return productRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Product", probe.getId()));
    }
}
