package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of InventoryMovementJpaEntity. Derived queries only; no business rules. */
public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    List<InventoryMovementJpaEntity> findByInventory_IdOrderByOccurredAtDesc(Long inventoryId);
}
