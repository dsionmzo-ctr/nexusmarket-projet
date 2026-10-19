package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;

/**
 * Warehouses are visible to ADMIN, LOGISTIC_OPERATOR and SUPERVISOR; a seller only sees its own.
 */
public class AuthorizeWarehouseAccessService {

    private final ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService;
    private final ValidateSellerOwnershipService validateSellerOwnershipService;

    public AuthorizeWarehouseAccessService(ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService,
            ValidateSellerOwnershipService validateSellerOwnershipService) {
        this.validateUserAuthorizationStatusService = validateUserAuthorizationStatusService;
        this.validateSellerOwnershipService = validateSellerOwnershipService;
    }

    public void execute(User user, Warehouse warehouse) {
        validateUserAuthorizationStatusService.execute(user);
        if (user.hasRole(SystemRole.SELLER)) {
            validateSellerOwnershipService.execute(user, warehouse.getSeller());
            return;
        }
        if (!(user.hasRole(SystemRole.ADMIN) || user.hasRole(SystemRole.LOGISTIC_OPERATOR)
                || user.hasRole(SystemRole.SUPERVISOR))) {
            throw new UnauthorizedOperationException("The role cannot access warehouses.");
        }
    }
}
