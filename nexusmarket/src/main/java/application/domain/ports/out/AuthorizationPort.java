package application.domain.ports.out;

import application.domain.models.MarketplaceResource;
import application.domain.models.User;

/**
 * Output port: external authorization information, reserved for decisions that cannot be taken
 * from the Domain Models already available. It does not replace normal Domain validation: rules
 * that depend only on the user's role are evaluated by the authorization domain services.
 */
public interface AuthorizationPort {

    boolean isAuthorized(User user, User target);

    boolean canOperateOn(User user, MarketplaceResource resource);

    boolean canApprove(User user, MarketplaceResource resource);
}
