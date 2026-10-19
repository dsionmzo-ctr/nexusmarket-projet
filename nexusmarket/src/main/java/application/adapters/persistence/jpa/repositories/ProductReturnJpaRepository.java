package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductReturnJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of ProductReturnJpaEntity. Derived queries only; no business rules. */
public interface ProductReturnJpaRepository extends JpaRepository<ProductReturnJpaEntity, Long> {

    List<ProductReturnJpaEntity> findByOrder_Id(Long orderId);
    List<ProductReturnJpaEntity> findByOrder_Buyer_Id(Long buyerId);
    List<ProductReturnJpaEntity> findByStatus(String status);
}
