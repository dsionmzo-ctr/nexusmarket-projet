package application.domain.services.authorization;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/**
 * A product can only be managed by the seller that owns it.
 */
public class AuthorizeProductOperationService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ValidateSellerOwnershipService validateSellerOwnershipService;

    public AuthorizeProductOperationService(AuthorizeOperationService authorizeOperationService,
            ValidateSellerOwnershipService validateSellerOwnershipService) {
        this.authorizeOperationService = authorizeOperationService;
        this.validateSellerOwnershipService = validateSellerOwnershipService;
    }

    public void execute(User user, Product product) {
        authorizeOperationService.execute(user, SystemRole.SELLER);
        validateSellerOwnershipService.execute(user, product.getSeller());
    }
}
