package application.domain.ports.out;

import application.domain.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Output port: persistence and queries of system users. Works with Domain Models only.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(User user);

    Optional<User> findByEmail(User user);

    Optional<User> findByIdentification(User user);

    boolean existsByEmail(User user);

    boolean existsByIdentification(User user);

    List<User> findAll();

    void update(User user);

    /** Atomically bumps the token version, invalidating every token issued so far. */
    User incrementAuthTokenVersion(User user);
}
