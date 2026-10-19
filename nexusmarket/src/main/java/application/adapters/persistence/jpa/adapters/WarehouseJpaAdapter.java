package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.WarehouseJpaRepository;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the warehouse repository port. */
@Repository
@Transactional
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final WarehouseJpaRepository warehouseJpaRepository;

    public WarehouseJpaAdapter(WarehouseJpaRepository warehouseJpaRepository) {
        this.warehouseJpaRepository = warehouseJpaRepository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        return WarehouseJpaMapper.toDomain(warehouseJpaRepository.save(WarehouseJpaMapper.toEntity(warehouse)));
    }

    @Override
    public Optional<Warehouse> findById(Warehouse warehouse) {
        return warehouseJpaRepository.findById(warehouse.getId()).map(WarehouseJpaMapper::toDomain);
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseJpaRepository.findAll().stream().map(WarehouseJpaMapper::toDomain).toList();
    }

    @Override
    public List<Warehouse> findBySeller(Seller seller) {
        return warehouseJpaRepository.findBySeller_Id(seller.getId()).stream().map(WarehouseJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Warehouse warehouse) {
        warehouseJpaRepository.save(WarehouseJpaMapper.toEntity(warehouse));
    }
}
