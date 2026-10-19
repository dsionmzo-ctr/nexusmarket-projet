package application.domain.services.authorization;

import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/**
 * Gate used by the business services: the user must be ACTIVE and have one of the allowed roles.
 */
public class AuthorizeOperationService {

    private final ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService;
    private final ValidateRoleAuthorizationService validateRoleAuthorizationService;

    public AuthorizeOperationService(ValidateUserAuthorizationStatusService validateUserAuthorizationStatusService,
            ValidateRoleAuthorizationService validateRoleAuthorizationService) {
        this.validateUserAuthorizationStatusService = validateUserAuthorizationStatusService;
        this.validateRoleAuthorizationService = validateRoleAuthorizationService;
    }

    public void execute(User user, SystemRole... allowedRoles) {
        validateUserAuthorizationStatusService.execute(user);
        validateRoleAuthorizationService.execute(user, allowedRoles);
    }
}
