package application.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** Registers the relational repositories with {@link RefreshingJpaRepository} as their base class. */
@Configuration
@EnableJpaRepositories(
        basePackages = "application.adapters.persistence.jpa.repositories",
        repositoryBaseClass = RefreshingJpaRepository.class)
public class JpaRepositoriesConfig {
}
