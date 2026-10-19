package application.adapters.persistence.jpa.adapters;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.UserJpaRepository;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Persistence adapter of the user repository port (PostgreSQL via Spring Data JPA). */
@Repository
@Transactional
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    public UserJpaAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        return UserJpaMapper.toDomain(userJpaRepository.save(UserJpaMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(User user) {
        return userJpaRepository.findById(user.getId()).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(User user) {
        return userJpaRepository.findByEmail(user.getEmail()).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByIdentification(User user) {
        return userJpaRepository.findByIdentification(user.getIdentification()).map(UserJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(User user) {
        return userJpaRepository.existsByEmail(user.getEmail());
    }

    @Override
    public boolean existsByIdentification(User user) {
        return userJpaRepository.existsByIdentification(user.getIdentification());
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(UserJpaMapper::toDomain).toList();
    }

    @Override
    public void update(User user) {
        userJpaRepository.save(UserJpaMapper.toEntity(user));
    }

    @Override
    public User incrementAuthTokenVersion(User user) {
        UserJpaEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new application.domain.exceptions.EntityNotFoundException("User", user.getId()));
        entity.setAuthTokenVersion(entity.getAuthTokenVersion() + 1);
        return UserJpaMapper.toDomain(userJpaRepository.save(entity));
    }
}
