package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point of the NexusMarket back end (hexagonal architecture, package root {@code application}). */
@SpringBootApplication
public class NexusMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusMarketApplication.class, args);
    }
}
