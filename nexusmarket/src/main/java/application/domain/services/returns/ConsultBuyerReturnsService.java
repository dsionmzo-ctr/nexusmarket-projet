package application.domain.services.returns;

import application.domain.models.ProductReturn;
import application.domain.models.User;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import java.util.List;

/**
 * Consults the returns of the authenticated buyer.
 */
public class ConsultBuyerReturnsService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final ProductReturnRepositoryPort productReturnRepositoryPort;

    public ConsultBuyerReturnsService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            ProductReturnRepositoryPort productReturnRepositoryPort) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.productReturnRepositoryPort = productReturnRepositoryPort;
    }

    public List<ProductReturn> execute(User user) {
        return productReturnRepositoryPort.findByBuyer(resolveAuthenticatedBuyerService.execute(user));
    }
}
