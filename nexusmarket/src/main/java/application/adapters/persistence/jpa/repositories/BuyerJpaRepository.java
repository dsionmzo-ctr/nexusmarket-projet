package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of BuyerJpaEntity. Derived queries only; no business rules. */
public interface BuyerJpaRepository extends JpaRepository<BuyerJpaEntity, Long> {

    Optional<BuyerJpaEntity> findByIdentification(String identification);
    boolean existsByIdentification(String identification);
    boolean existsByEmail(String email);
}
