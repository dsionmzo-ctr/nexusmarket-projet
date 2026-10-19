package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.OrderJpaRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Seller;
import application.domain.ports.out.OrderRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the order repository port (order + lines + stock allocations). */
@Repository
@Transactional
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;

    public OrderJpaAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        return OrderJpaMapper.toDomain(orderJpaRepository.save(OrderJpaMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Order order) {
        return orderJpaRepository.findById(order.getId()).map(OrderJpaMapper::toDomain);
    }

    @Override
    public List<Order> findByBuyer(Buyer buyer) {
        return orderJpaRepository.findByBuyer_IdOrderByCreatedAtDesc(buyer.getId()).stream()
                .map(OrderJpaMapper::toDomain).toList();
    }

    @Override
    public List<Order> findBySeller(Seller seller) {
        return orderJpaRepository.findDistinctByItems_Seller_IdOrderByCreatedAtDesc(seller.getId()).stream()
                .map(OrderJpaMapper::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAllByOrderByCreatedAtDesc().stream().map(OrderJpaMapper::toDomain).toList();
    }

    @Override
    public void update(Order order) {
        orderJpaRepository.save(OrderJpaMapper.toEntity(order));
    }
}
