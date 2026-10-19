package application.domain.services.returns;

import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.services.authorization.AuthorizeOperationService;
import application.domain.valueobjects.SystemRole;
import java.util.List;

/**
 * Consults the refunds, optionally filtered by status (ADMIN and SUPERVISOR).
 */
public class ConsultRefundsService {

    private final AuthorizeOperationService authorizeOperationService;
    private final RefundRepositoryPort refundRepositoryPort;

    public ConsultRefundsService(AuthorizeOperationService authorizeOperationService,
            RefundRepositoryPort refundRepositoryPort) {
        this.authorizeOperationService = authorizeOperationService;
        this.refundRepositoryPort = refundRepositoryPort;
    }

    public List<Refund> execute(User user, Refund filter) {
        authorizeOperationService.execute(user, SystemRole.ADMIN, SystemRole.SUPERVISOR);
        return refundRepositoryPort.findByStatus(filter == null ? new Refund() : filter);
    }
}
