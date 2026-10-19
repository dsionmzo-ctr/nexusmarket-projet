package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of ProductJpaEntity. Derived queries only; no business rules. */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findByStatus(String status);
    List<ProductJpaEntity> findBySeller_Id(Long sellerId);
    Optional<ProductJpaEntity> findByVariants_Id(Long variantId);
    boolean existsByVariants_Sku(String sku);
}
