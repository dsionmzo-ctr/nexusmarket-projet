package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of SellerJpaEntity. Derived queries only; no business rules. */
public interface SellerJpaRepository extends JpaRepository<SellerJpaEntity, Long> {

    Optional<SellerJpaEntity> findByIdentification(String identification);
    boolean existsByIdentification(String identification);
    boolean existsByEmail(String email);
}
