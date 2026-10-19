package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Seller;
import application.domain.models.User;

/**
 * Validates that the user is the seller that owns the resource.
 */
public class ValidateSellerOwnershipService {


    public void execute(User user, Seller owner) {
        Seller own = user == null ? null : user.getSeller();
        if (own == null || owner == null || own.getId() == null || !own.getId().equals(owner.getId())) {
            throw new UnauthorizedOperationException("The resource does not belong to the authenticated seller.");
        }
    }
}
