package application.domain.services.buyer;

import application.domain.models.Buyer;
import application.domain.models.User;

/**
 * Consults the profile of the authenticated buyer (a buyer never reads other buyers).
 */
public class ConsultBuyerProfileService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;

    public ConsultBuyerProfileService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
    }

    public Buyer execute(User user) {
        return resolveAuthenticatedBuyerService.execute(user);
    }
}
