package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidRefundException;
import application.domain.models.Refund;
import application.domain.ports.out.RefundRepositoryPort;

/**
 * Loads a refund by its identifier or fails with EntityNotFoundException.
 */
public class FindRefundService {

    private final RefundRepositoryPort refundRepositoryPort;

    public FindRefundService(RefundRepositoryPort refundRepositoryPort) {
        this.refundRepositoryPort = refundRepositoryPort;
    }

    public Refund execute(Refund probe) {
        if (probe == null || probe.getId() == null) {
            throw new InvalidRefundException("Refund identifier must be provided.");
        }
        return refundRepositoryPort.findById(probe)
                .orElseThrow(() -> new EntityNotFoundException("Refund", probe.getId()));
    }
}
