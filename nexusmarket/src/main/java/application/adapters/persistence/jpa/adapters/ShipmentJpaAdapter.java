package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.persistence.jpa.repositories.ShipmentJpaRepository;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the shipment repository port. */
@Repository
@Transactional
public class ShipmentJpaAdapter implements ShipmentRepositoryPort {

    private final ShipmentJpaRepository shipmentJpaRepository;

    public ShipmentJpaAdapter(ShipmentJpaRepository shipmentJpaRepository) {
        this.shipmentJpaRepository = shipmentJpaRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        return ShipmentJpaMapper.toDomain(shipmentJpaRepository.save(ShipmentJpaMapper.toEntity(shipment)));
    }

    @Override
    public Optional<Shipment> findById(Shipment shipment) {
        return shipmentJpaRepository.findById(shipment.getId()).map(ShipmentJpaMapper::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrder(Order order) {
        return shipmentJpaRepository.findByOrder_Id(order.getId()).map(ShipmentJpaMapper::toDomain);
    }

    /** A filter without status returns every shipment. */
    @Override
    public List<Shipment> findByStatus(Shipment shipment) {
        if (shipment.getStatus() == null) {
            return shipmentJpaRepository.findAll().stream().map(ShipmentJpaMapper::toDomain).toList();
        }
        return shipmentJpaRepository.findByStatus(shipment.getStatus().getCode()).stream()
                .map(ShipmentJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Shipment shipment) {
        shipmentJpaRepository.save(ShipmentJpaMapper.toEntity(shipment));
    }
}
