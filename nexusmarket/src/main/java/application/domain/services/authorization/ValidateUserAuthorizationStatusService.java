package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.UserStatus;

/**
 * Validates that a user is operationally ACTIVE before performing protected operations
 * (RG-01: every operation is executed by an authenticated user).
 */
public class ValidateUserAuthorizationStatusService {


    public void execute(User user) {
        if (user == null) {
            throw new UnauthorizedOperationException("User must be provided.");
        }
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UnauthorizedOperationException(
                    "User " + user.getEmail() + " is not active and cannot perform operations.");
        }
    }
}
