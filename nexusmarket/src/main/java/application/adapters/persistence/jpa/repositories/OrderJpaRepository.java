package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of OrderJpaEntity. Derived queries only; no business rules. */
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    List<OrderJpaEntity> findByBuyer_IdOrderByCreatedAtDesc(Long buyerId);
    List<OrderJpaEntity> findDistinctByItems_Seller_IdOrderByCreatedAtDesc(Long sellerId);
    List<OrderJpaEntity> findAllByOrderByCreatedAtDesc();
}
