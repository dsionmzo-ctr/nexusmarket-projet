package application.domain.services.returns;

import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.buyer.ResolveAuthenticatedBuyerService;
import java.util.List;

/**
 * Consults the refunds of the authenticated buyer.
 */
public class ConsultBuyerRefundsService {

    private final ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService;
    private final RefundRepositoryPort refundRepositoryPort;

    public ConsultBuyerRefundsService(ResolveAuthenticatedBuyerService resolveAuthenticatedBuyerService,
            RefundRepositoryPort refundRepositoryPort) {
        this.resolveAuthenticatedBuyerService = resolveAuthenticatedBuyerService;
        this.refundRepositoryPort = refundRepositoryPort;
    }

    public List<Refund> execute(User user) {
        return refundRepositoryPort.findByBuyer(resolveAuthenticatedBuyerService.execute(user));
    }
}
