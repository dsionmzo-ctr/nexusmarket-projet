package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ProductReturn;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of product returns.
 */
public interface ProductReturnRepositoryPort {

    ProductReturn save(ProductReturn productReturn);

    Optional<ProductReturn> findById(ProductReturn productReturn);

    List<ProductReturn> findByOrder(Order order);

    List<ProductReturn> findByBuyer(Buyer buyer);

    /** Returns with the status of the supplied model. */
    List<ProductReturn> findByStatus(ProductReturn productReturn);

    void update(ProductReturn productReturn);
}
