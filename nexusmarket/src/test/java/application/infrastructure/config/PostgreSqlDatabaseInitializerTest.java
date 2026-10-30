package application.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgreSqlDatabaseInitializerTest {

    @Test
    void derivesAdminUrlAndKeepsConnectionOptions() {
        PostgreSqlDatabaseInitializer.DatabaseTarget target =
                PostgreSqlDatabaseInitializer.parseTarget(
                        "jdbc:postgresql://localhost:5432/NexusMarket?sslmode=require");

        assertEquals("NexusMarket", target.name());
        assertEquals("jdbc:postgresql://localhost:5432/postgres?sslmode=require", target.adminUrl());
    }

    @Test
    void rejectsDatabaseNamesLongerThanPostgreSqlLimit() {
        String longName = "a".repeat(64);

        assertThrows(IllegalStateException.class,
                () -> PostgreSqlDatabaseInitializer.parseTarget(
                        "jdbc:postgresql://localhost:5432/" + longName));
    }

    @Test
    void rejectsJdbcUrlsWithoutDatabaseName() {
        assertThrows(IllegalStateException.class,
                () -> PostgreSqlDatabaseInitializer.parseTarget("jdbc:postgresql://localhost:5432"));
    }
}
