package application.domain.ports.out;

import application.domain.models.Seller;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of sellers.
 */
public interface SellerRepositoryPort {

    Seller save(Seller seller);

    Optional<Seller> findById(Seller seller);

    Optional<Seller> findByIdentification(Seller seller);

    boolean existsByIdentification(Seller seller);

    boolean existsByEmail(Seller seller);

    List<Seller> findAll();

    void update(Seller seller);
}
