package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Spring Data repository of InvoiceJpaEntity. Derived queries only; no business rules. */
public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, Long> {

    Optional<InvoiceJpaEntity> findByOrder_Id(Long orderId);
    @Query("select coalesce(max(i.id), 0) + 1 from InvoiceJpaEntity i")
    Long nextSequenceValue();
}
