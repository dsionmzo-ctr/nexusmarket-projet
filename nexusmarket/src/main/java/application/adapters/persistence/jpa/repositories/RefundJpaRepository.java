package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of RefundJpaEntity. Derived queries only; no business rules. */
public interface RefundJpaRepository extends JpaRepository<RefundJpaEntity, Long> {

    Optional<RefundJpaEntity> findByProductReturn_Id(Long returnId);
    List<RefundJpaEntity> findByProductReturn_Order_Buyer_Id(Long buyerId);
    List<RefundJpaEntity> findByStatus(String status);
}
