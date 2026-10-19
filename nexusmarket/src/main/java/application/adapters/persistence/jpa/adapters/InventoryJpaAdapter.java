package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.repositories.InventoryJpaRepository;
import application.domain.models.Inventory;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the inventory repository port. */
@Repository
@Transactional
public class InventoryJpaAdapter implements InventoryRepositoryPort {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryJpaAdapter(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        return InventoryJpaMapper.toDomain(inventoryJpaRepository.save(InventoryJpaMapper.toEntity(inventory)));
    }

    @Override
    public Optional<Inventory> findById(Inventory inventory) {
        return inventoryJpaRepository.findById(inventory.getId()).map(InventoryJpaMapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByVariantAndWarehouse(Inventory inventory) {
        return inventoryJpaRepository.findByVariant_IdAndWarehouse_Id(
                inventory.getVariant().getId(), inventory.getWarehouse().getId()).map(InventoryJpaMapper::toDomain);
    }

    @Override
    public List<Inventory> findByWarehouse(Warehouse warehouse) {
        return inventoryJpaRepository.findByWarehouse_Id(warehouse.getId()).stream().map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findByVariant(ProductVariant variant) {
        return inventoryJpaRepository.findByVariant_Id(variant.getId()).stream().map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findBySeller(Seller seller) {
        return inventoryJpaRepository.findByProduct_Seller_Id(seller.getId()).stream().map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryJpaRepository.findAll().stream().map(InventoryJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Inventory inventory) {
        inventoryJpaRepository.save(InventoryJpaMapper.toEntity(inventory));
    }
}
