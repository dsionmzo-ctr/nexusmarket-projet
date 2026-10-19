package application.adapters.security;

import application.domain.models.MarketplaceResource;
import application.domain.models.User;
import application.domain.ports.out.AuthorizationPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Component;

/**
 * External authorization decisions based only on the user's role. Ownership and business-scope rules
 * stay in the authorization domain services; this adapter answers the coarse questions of the port.
 */
@Component
public class RoleAuthorizationAdapter implements AuthorizationPort {

    @Override
    public boolean isAuthorized(User user, User target) {
        if (user == null || target == null || !user.isActive()) {
            return false;
        }
        return SystemRole.ADMIN.equals(user.getRole()) || SystemRole.SUPERVISOR.equals(user.getRole())
                || (user.getId() != null && user.getId().equals(target.getId()));
    }

    @Override
    public boolean canOperateOn(User user, MarketplaceResource resource) {
        if (user == null || resource == null || !user.isActive()) {
            return false;
        }
        return !SystemRole.SUPERVISOR.equals(user.getRole());
    }

    @Override
    public boolean canApprove(User user, MarketplaceResource resource) {
        return user != null && user.isActive() && SystemRole.ADMIN.equals(user.getRole()) && resource != null;
    }
}
