package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;

/**
 * Public catalog: consults one PUBLISHED product. A product that is not published is reported as not found.
 */
public class ConsultPublishedProductService {

    private final FindProductService findProductService;

    public ConsultPublishedProductService(FindProductService findProductService) {
        this.findProductService = findProductService;
    }

    public Product execute(Product probe) {
        Product product = findProductService.execute(probe);
        if (!product.isPublished()) {
            throw new EntityNotFoundException("Product", probe.getId());
        }
        return product;
    }
}
