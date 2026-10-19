package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository of WarehouseJpaEntity. Derived queries only; no business rules. */
public interface WarehouseJpaRepository extends JpaRepository<WarehouseJpaEntity, Long> {

    List<WarehouseJpaEntity> findBySeller_Id(Long sellerId);
}
