package application.domain.ports.out;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Seller;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of orders.
 */
public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(Order order);

    List<Order> findByBuyer(Buyer buyer);

    /** Orders that contain at least one line of the seller. */
    List<Order> findBySeller(Seller seller);

    List<Order> findAll();

    void update(Order order);
}
