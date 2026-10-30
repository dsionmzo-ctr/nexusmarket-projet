package application.infrastructure.config;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class of every Spring Data JPA repository. The persistence mappers reference other aggregates with
 * identifier-only entities ({@code XxxJpaMapper.ref}); after a save those references would still be empty when
 * the stored entity is mapped back to the domain. Flushing and refreshing the saved entity reloads every
 * association from the database, so the adapters always return complete Domain Models.
 */
public class RefreshingJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public RefreshingJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        S saved = super.save(entity);
        entityManager.flush();
        entityManager.refresh(saved);
        return saved;
    }
}
