package application.infrastructure.config;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public final class PostgreSqlDatabaseInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlDatabaseInitializer.class);
    private static final String DEFAULT_DATABASE = "postgres";
    private static final int MAX_DATABASE_NAME_BYTES = 63;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        if (!environment.getProperty("nexusmarket.datasource.create-database", Boolean.class, true)) {
            return;
        }

        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        DatabaseTarget target = parseTarget(jdbcUrl);

        try (Connection connection = DriverManager.getConnection(target.adminUrl(), username, password)) {
            if (databaseExists(connection, target.name())) {
                LOGGER.info("PostgreSQL database '{}' already exists", target.name());
                return;
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE " + quoteIdentifier(target.name()));
                LOGGER.info("Created PostgreSQL database '{}'", target.name());
            } catch (SQLException exception) {
                if (!"42P04".equals(exception.getSQLState()) || !databaseExists(connection, target.name())) {
                    throw exception;
                }
                LOGGER.info("PostgreSQL database '{}' was created by another application instance", target.name());
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to ensure PostgreSQL database '" + target.name()
                            + "' exists. Verify the database server, credentials, and CREATEDB permission.",
                    exception);
        }
    }

    static DatabaseTarget parseTarget(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException(
                    "Database auto-creation requires a PostgreSQL JDBC URL in the form "
                            + "jdbc:postgresql://host:port/database.");
        }

        URI uri;
        try {
            uri = URI.create(jdbcUrl.substring("jdbc:".length()));
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid PostgreSQL JDBC URL.", exception);
        }

        String path = uri.getPath();
        if (!"postgresql".equals(uri.getScheme()) || uri.getRawAuthority() == null
                || path == null || path.length() < 2 || !path.startsWith("/")) {
            throw new IllegalStateException(
                    "Database auto-creation requires a PostgreSQL JDBC URL with a database name.");
        }

        String databaseName = path.substring(1);
        if (databaseName.getBytes(StandardCharsets.UTF_8).length > MAX_DATABASE_NAME_BYTES) {
            throw new IllegalStateException("PostgreSQL database names cannot exceed 63 UTF-8 bytes.");
        }

        StringBuilder adminUrl = new StringBuilder("jdbc:postgresql://")
                .append(uri.getRawAuthority())
                .append('/')
                .append(DEFAULT_DATABASE);
        if (uri.getRawQuery() != null) {
            adminUrl.append('?').append(uri.getRawQuery());
        }

        return new DatabaseTarget(databaseName, adminUrl.toString());
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        try (var statement = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    record DatabaseTarget(String name, String adminUrl) {
    }
}
