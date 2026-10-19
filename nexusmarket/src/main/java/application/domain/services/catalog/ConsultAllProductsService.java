package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults every product of the catalog, in any status (ADMIN and SUPERVISOR).
 */
public class ConsultAllProductsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ProductRepositoryPort productRepositoryPort;

    public ConsultAllProductsService(AuthorizeOperationService authorizeOperationService,
            ProductRepositoryPort productRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.productRepositoryPort = productRepositoryPort;
    }

    public List<Product> execute(User user) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return productRepositoryPort.findAll();
    }
}
