package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/**
 * Validates that the role of a user is one of the roles allowed for an operation
 * (RG-02 and RG-03: a participant cannot manage information outside its role).
 */
public class ValidateRoleAuthorizationService {


    public void execute(User user, SystemRole... allowedRoles) {
        if (user == null || user.getRole() == null) {
            throw new UnauthorizedOperationException("User role must be provided.");
        }
        for (SystemRole allowed : allowedRoles) {
            if (allowed.equals(user.getRole())) {
                return;
            }
        }
        throw new UnauthorizedOperationException(
                "Role " + user.getRole().getCode() + " is not allowed to perform this operation.");
    }
}
