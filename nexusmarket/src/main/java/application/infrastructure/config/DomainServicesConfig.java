package application.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.time.Clock;

/**
 * Registers the domain services as Spring beans without annotating them: the domain layer stays free
 * of framework dependencies and is wired from the outside. Every class of
 * {@code application.domain.services} whose name ends with {@code Service} becomes a singleton bean.
 */
@Configuration
@ComponentScan(
        basePackages = "application.domain.services",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Service"))
public class DomainServicesConfig {

    /** Time source injected into the domain services (replaceable in tests). */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
