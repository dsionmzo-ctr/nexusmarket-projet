package application.domain.services.buyer;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;

/**
 * Resolves the Buyer profile of the authenticated user (role BUYER, active user) from the repository.
 */
public class ResolveAuthenticatedBuyerService {

    private final AuthorizeOperationService authorizeOperationService;
    private final BuyerRepositoryPort buyerRepositoryPort;

    public ResolveAuthenticatedBuyerService(AuthorizeOperationService authorizeOperationService,
            BuyerRepositoryPort buyerRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.buyerRepositoryPort = buyerRepositoryPort;
    }

    public Buyer execute(User user) {
        authorizeOperationService.execute(user, SystemRole.BUYER);
        if (user.getBuyer() == null) {
            throw new UnauthorizedOperationException("The user has no buyer profile.");
        }
        return buyerRepositoryPort.findById(user.getBuyer())
                .orElseThrow(() -> new EntityNotFoundException("Buyer", user.getBuyer().getId()));
    }
}
