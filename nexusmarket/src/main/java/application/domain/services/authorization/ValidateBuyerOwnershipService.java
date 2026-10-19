package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.User;

/**
 * Validates that the user is the buyer that owns the resource.
 */
public class ValidateBuyerOwnershipService {


    public void execute(User user, Buyer owner) {
        Buyer own = user == null ? null : user.getBuyer();
        if (own == null || owner == null || own.getId() == null || !own.getId().equals(owner.getId())) {
            throw new UnauthorizedOperationException("The resource does not belong to the authenticated buyer.");
        }
    }
}
