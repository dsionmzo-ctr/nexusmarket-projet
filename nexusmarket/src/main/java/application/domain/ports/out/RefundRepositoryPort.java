package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.ProductReturn;
import application.domain.models.Refund;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of refunds.
 */
public interface RefundRepositoryPort {

    Refund save(Refund refund);

    Optional<Refund> findById(Refund refund);

    Optional<Refund> findByReturn(ProductReturn productReturn);

    List<Refund> findByBuyer(Buyer buyer);

    /** Refunds with the status of the supplied model, or every refund when its status is null. */
    List<Refund> findByStatus(Refund refund);

    void update(Refund refund);
}
