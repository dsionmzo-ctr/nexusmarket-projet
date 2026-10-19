package application.domain.ports.out;

import application.domain.models.Inventory;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of distributed inventory.
 */
public interface InventoryRepositoryPort {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Inventory inventory);

    /** Inventory of one variant in one warehouse (the variant and the warehouse come from the model). */
    Optional<Inventory> findByVariantAndWarehouse(Inventory inventory);

    List<Inventory> findByWarehouse(Warehouse warehouse);

    List<Inventory> findByVariant(ProductVariant variant);

    /** Inventory records of every product of a seller. */
    List<Inventory> findBySeller(Seller seller);

    List<Inventory> findAll();

    void update(Inventory inventory);
}
