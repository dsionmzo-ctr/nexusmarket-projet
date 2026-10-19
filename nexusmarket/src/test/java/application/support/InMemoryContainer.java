package application.support;

import application.domain.models.User;
import application.domain.ports.out.AuthorizationPort;
import application.domain.ports.out.BusinessConfigurationPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.PasswordServicePort;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal dependency-injection container for tests: output ports are the in-memory fakes, the domain
 * services and the use case adapters are created by constructor injection, as Spring does.
 */
public final class InMemoryContainer {

    public final MutableClock clock = new MutableClock(Instant.parse("2026-10-01T10:00:00Z"));
    public final InMemoryPorts.Users users = new InMemoryPorts.Users();
    public final InMemoryPorts.Buyers buyers = new InMemoryPorts.Buyers();
    public final InMemoryPorts.Sellers sellers = new InMemoryPorts.Sellers();
    public final InMemoryPorts.Warehouses warehouses = new InMemoryPorts.Warehouses();
    public final InMemoryPorts.Products products = new InMemoryPorts.Products();
    public final InMemoryPorts.Inventories inventories = new InMemoryPorts.Inventories();
    public final InMemoryPorts.Movements movements = new InMemoryPorts.Movements();
    public final InMemoryPorts.Carts carts = new InMemoryPorts.Carts();
    public final InMemoryPorts.Orders orders = new InMemoryPorts.Orders();
    public final InMemoryPorts.Invoices invoices = new InMemoryPorts.Invoices();
    public final InMemoryPorts.Shipments shipments = new InMemoryPorts.Shipments();
    public final InMemoryPorts.Returns returns = new InMemoryPorts.Returns();
    public final InMemoryPorts.Refunds refunds = new InMemoryPorts.Refunds();
    public final InMemoryPorts.Operations operations = new InMemoryPorts.Operations();
    public final InMemoryPorts.AuditLogs auditLogs = new InMemoryPorts.AuditLogs();
    public final InMemoryPorts.Notifications notifications = new InMemoryPorts.Notifications();

    private final Map<Class<?>, Object> beans = new HashMap<>();

    public InMemoryContainer() {
        register(Clock.class, clock);
        register(application.domain.ports.out.UserRepositoryPort.class, users);
        register(application.domain.ports.out.BuyerRepositoryPort.class, buyers);
        register(application.domain.ports.out.SellerRepositoryPort.class, sellers);
        register(application.domain.ports.out.WarehouseRepositoryPort.class, warehouses);
        register(application.domain.ports.out.ProductRepositoryPort.class, products);
        register(application.domain.ports.out.InventoryRepositoryPort.class, inventories);
        register(application.domain.ports.out.InventoryMovementRepositoryPort.class, movements);
        register(application.domain.ports.out.CartRepositoryPort.class, carts);
        register(application.domain.ports.out.OrderRepositoryPort.class, orders);
        register(application.domain.ports.out.InvoiceRepositoryPort.class, invoices);
        register(application.domain.ports.out.ShipmentRepositoryPort.class, shipments);
        register(application.domain.ports.out.ProductReturnRepositoryPort.class, returns);
        register(application.domain.ports.out.RefundRepositoryPort.class, refunds);
        register(application.domain.ports.out.OperationRepositoryPort.class, operations);
        register(application.domain.ports.out.AuditLogRepositoryPort.class, auditLogs);
        register(application.domain.ports.out.NotificationPort.class, notifications);
        register(PasswordServicePort.class, new PasswordServicePort() {
            @Override
            public boolean matches(User user) {
                return user.getPasswordHash() != null && user.getPasswordHash().equals("hash:" + user.getRawPassword());
            }

            @Override
            public String encrypt(User user) {
                return "hash:" + user.getRawPassword();
            }
        });
        register(JwtServicePort.class, new JwtServicePort() {
            @Override
            public String generateToken(User user) {
                return "token-" + user.getId() + "-v" + user.getAuthTokenVersion();
            }

            @Override
            public long getExpirationSeconds() {
                return 3600;
            }
        });
        register(BusinessConfigurationPort.class, new BusinessConfigurationPort() {
            @Override
            public BigDecimal getTaxRate() {
                return new BigDecimal("0.19");
            }

            @Override
            public Integer getReturnWindowDays() {
                return 30;
            }
        });
        register(AuthorizationPort.class, new AuthorizationPort() {
            @Override
            public boolean isAuthorized(User user, User target) {
                return true;
            }

            @Override
            public boolean canOperateOn(User user, application.domain.models.MarketplaceResource resource) {
                return true;
            }

            @Override
            public boolean canApprove(User user, application.domain.models.MarketplaceResource resource) {
                return true;
            }
        });
    }

    public <T> void register(Class<T> type, T instance) {
        beans.put(type, instance);
    }

    /** Returns the bean of the given type, creating it (and its dependencies) on first use. */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        Object existing = beans.get(type);
        if (existing != null) {
            return (T) existing;
        }
        if (type.isInterface()) {
            throw new IllegalStateException("No bean registered for " + type.getName());
        }
        try {
            Constructor<?> constructor = type.getConstructors()[0];
            Object[] arguments = new Object[constructor.getParameterCount()];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = get(parameterTypes[i]);
            }
            Object bean = constructor.newInstance(arguments);
            beans.put(type, bean);
            return (T) bean;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot create " + type.getName(), ex);
        }
    }
}
