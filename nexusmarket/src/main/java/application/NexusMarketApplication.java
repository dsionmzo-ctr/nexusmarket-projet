package application;

import application.infrastructure.config.PostgreSqlDatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point of the NexusMarket back end (hexagonal architecture, package root {@code application}). */
@SpringBootApplication
public class NexusMarketApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(NexusMarketApplication.class);
        application.addInitializers(new PostgreSqlDatabaseInitializer());
        application.run(args);
    }
}
