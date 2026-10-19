package application.domain.services.authorization;

import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/**
 * Inventory is administered by a LOGISTIC_OPERATOR (any warehouse) or by a SELLER only over its own
 * products stored in its own warehouses.
 */
public class AuthorizeInventoryOperationService {

    private final AuthorizeOperationService authorizeOperationService;
    private final ValidateSellerOwnershipService validateSellerOwnershipService;

    public AuthorizeInventoryOperationService(AuthorizeOperationService authorizeOperationService,
            ValidateSellerOwnershipService validateSellerOwnershipService) {
        this.authorizeOperationService = authorizeOperationService;
        this.validateSellerOwnershipService = validateSellerOwnershipService;
    }

    public void execute(User user, Inventory inventory) {
        authorizeOperationService.execute(user, SystemRole.SELLER, SystemRole.LOGISTIC_OPERATOR);
        if (user.hasRole(SystemRole.SELLER)) {
            validateSellerOwnershipService.execute(user, inventory.getProduct().getSeller());
            validateSellerOwnershipService.execute(user, inventory.getWarehouse().getSeller());
        }
    }
}
