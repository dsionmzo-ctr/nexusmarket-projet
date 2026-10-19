package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.InventoryMovementJpaMapper;
import application.adapters.persistence.jpa.repositories.InventoryMovementJpaRepository;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the inventory movement repository port (append-only history). */
@Repository
@Transactional
public class InventoryMovementJpaAdapter implements InventoryMovementRepositoryPort {

    private final InventoryMovementJpaRepository inventoryMovementJpaRepository;

    public InventoryMovementJpaAdapter(InventoryMovementJpaRepository inventoryMovementJpaRepository) {
        this.inventoryMovementJpaRepository = inventoryMovementJpaRepository;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        return InventoryMovementJpaMapper.toDomain(
                inventoryMovementJpaRepository.save(InventoryMovementJpaMapper.toEntity(movement)));
    }

    @Override
    public List<InventoryMovement> findByInventory(Inventory inventory) {
        return inventoryMovementJpaRepository.findByInventory_IdOrderByOccurredAtDesc(inventory.getId()).stream()
                .map(InventoryMovementJpaMapper::toDomain).toList();
    }
}
