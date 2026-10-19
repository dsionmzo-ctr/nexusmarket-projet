package application.domain.ports.out;

import application.domain.models.Seller;
import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of warehouses.
 */
public interface WarehouseRepositoryPort {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(Warehouse warehouse);

    List<Warehouse> findAll();

    List<Warehouse> findBySeller(Seller seller);

    void update(Warehouse warehouse);
}
