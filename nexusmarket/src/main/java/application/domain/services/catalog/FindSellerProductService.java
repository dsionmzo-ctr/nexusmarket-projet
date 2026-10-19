package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.services.authorization.AuthorizeProductOperationService;

/**
 * Loads a product and verifies that the authenticated user is the seller that owns it.
 */
public class FindSellerProductService {

    private final FindProductService findProductService;
    private final AuthorizeProductOperationService authorizeProductOperationService;

    public FindSellerProductService(FindProductService findProductService,
            AuthorizeProductOperationService authorizeProductOperationService) {
        this.findProductService = findProductService;
        this.authorizeProductOperationService = authorizeProductOperationService;
    }

    public Product execute(User user, Product probe) {
        Product product = findProductService.execute(probe);
        authorizeProductOperationService.execute(user, product);
        return product;
    }
}
