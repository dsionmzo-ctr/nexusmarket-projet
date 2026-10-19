package application.domain.services.seller;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;

/**
 * Resolves the Seller profile of the authenticated user (role SELLER, active user) from the repository.
 */
public class ResolveAuthenticatedSellerService {

    private final AuthorizeOperationService authorizeOperationService;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ResolveAuthenticatedSellerService(AuthorizeOperationService authorizeOperationService,
            SellerRepositoryPort sellerRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    public Seller execute(User user) {
        authorizeOperationService.execute(user, SystemRole.SELLER);
        if (user.getSeller() == null) {
            throw new UnauthorizedOperationException("The user has no seller profile.");
        }
        return sellerRepositoryPort.findById(user.getSeller())
                .orElseThrow(() -> new EntityNotFoundException("Seller", user.getSeller().getId()));
    }
}
