package application.domain.ports.out;

import application.domain.models.Buyer;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of buyers.
 */
public interface BuyerRepositoryPort {

    Buyer save(Buyer buyer);

    Optional<Buyer> findById(Buyer buyer);

    Optional<Buyer> findByIdentification(Buyer buyer);

    boolean existsByIdentification(Buyer buyer);

    boolean existsByEmail(Buyer buyer);

    List<Buyer> findAll();

    void update(Buyer buyer);
}
