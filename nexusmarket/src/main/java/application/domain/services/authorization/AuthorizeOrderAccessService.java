package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/**
 * Access to an order: the buyer that owns it, a seller only to its own lines (returned as a restricted view),
 * and the operational roles (LOGISTIC_OPERATOR, ADMIN, SUPERVISOR) to all.
 */
public class AuthorizeOrderAccessService {

    private final ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService;
    private final ValidateBuyerOwnershipService validateBuyerOwnershipService;

    public AuthorizeOrderAccessService(ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService,
            ValidateBuyerOwnershipService validateBuyerOwnershipService) {
        this.validateUserAuthorizationStatusService = validateUserAuthorizationStatusService;
        this.validateBuyerOwnershipService = validateBuyerOwnershipService;
    }

    public Order execute(User user, Order order) {
        validateUserAuthorizationStatusService.execute(user);
        if (user.hasRole(SystemRole.BUYER)) {
            validateBuyerOwnershipService.execute(user, order.getBuyer());
            return order;
        }
        if (user.hasRole(SystemRole.SELLER)) {
            if (!order.containsSeller(user.getSeller())) {
                throw new UnauthorizedOperationException("The order does not contain products of the authenticated seller.");
            }
            return order.forSeller(user.getSeller());
        }
        if (user.hasRole(SystemRole.LOGISTIC_OPERATOR) || user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.SUPERVISOR)) {
            return order;
        }
        throw new UnauthorizedOperationException("The role cannot access orders.");
    }
}
