package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of InventoryJpaEntity. Derived queries only; no business rules. */
public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, Long> {

    Optional<InventoryJpaEntity> findByVariant_IdAndWarehouse_Id(Long variantId, Long warehouseId);
    List<InventoryJpaEntity> findByWarehouse_Id(Long warehouseId);
    List<InventoryJpaEntity> findByVariant_Id(Long variantId);
    List<InventoryJpaEntity> findByProduct_Seller_Id(Long sellerId);
}
