package application.infrastructure.seed;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.InventoryMovement;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdminPort;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.SellerPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads demonstration data on an empty database (one user per role, a seller with catalog and stock, a
 * buyer). Only the very first administrator is created through the output ports; everything else goes
 * through the input ports exactly as a real client would, so operations and audit logs are also
 * generated. It is idempotent and can be disabled with {@code nexusmarket.seed.enabled=false}.
 */
@Component
public class DatabaseSeeder implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final boolean enabled;
    private final String password;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final PublicAccessPort publicAccessPort;
    private final AdminPort adminPort;
    private final SellerPort sellerPort;

    public DatabaseSeeder(@Value("${nexusmarket.seed.enabled:true}") boolean enabled,
            @Value("${nexusmarket.seed.password:Password123*}") String password,
            UserRepositoryPort userRepositoryPort, PasswordServicePort passwordServicePort,
            PublicAccessPort publicAccessPort, AdminPort adminPort, SellerPort sellerPort) {
        this.enabled = enabled;
        this.password = password;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.publicAccessPort = publicAccessPort;
        this.adminPort = adminPort;
        this.sellerPort = sellerPort;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        User probe = new User();
        probe.setEmail("admin@nexusmarket.com");
        if (userRepositoryPort.existsByEmail(probe)) {
            LOGGER.info("Seed data already present; nothing to do.");
            return;
        }
        LOGGER.warn("Loading demonstration data with the default seed password. Disable it in production "
                + "(nexusmarket.seed.enabled=false).");

        User admin = createFirstAdmin();
        adminPort.registerStaffUser(admin, staff("1000000002", "Sofia Supervisor", "supervisor@nexusmarket.com",
                SystemRole.SUPERVISOR));
        adminPort.registerStaffUser(admin, staff("1000000003", "Luis Logistica", "operator@nexusmarket.com",
                SystemRole.LOGISTIC_OPERATOR));

        Warehouse marketplaceWarehouse = new Warehouse();
        marketplaceWarehouse.setName("Marketplace Central Warehouse");
        marketplaceWarehouse.setType(WarehouseType.MARKETPLACE);
        marketplaceWarehouse.setAddress(new Address("Calle 10 # 20-30", "Medellin", "Colombia"));
        adminPort.createWarehouse(admin, marketplaceWarehouse);

        Seller seller = new Seller();
        seller.setIdentification("900123456");
        seller.setName("Tech Store SAS");
        seller.setEmail("seller@nexusmarket.com");
        seller.setBusinessName("Tech Store");
        Warehouse firstWarehouse = new Warehouse();
        firstWarehouse.setName("Tech Store Warehouse");
        firstWarehouse.setAddress(new Address("Carrera 43A # 1-50", "Medellin", "Colombia"));
        Seller savedSeller = adminPort.registerSeller(admin, seller, firstWarehouse);
        User sellerAccount = new User();
        sellerAccount.setSeller(savedSeller);
        sellerAccount.setRawPassword(password);
        adminPort.registerSellerUser(admin, sellerAccount);

        Buyer buyer = new Buyer();
        buyer.setIdentification("1017123456");
        buyer.setName("Juan Perez");
        buyer.setEmail("buyer@nexusmarket.com");
        buyer.setPrimaryAddress(new Address("Calle 50 # 40-20", "Medellin", "Colombia"));
        Buyer savedBuyer = publicAccessPort.registerBuyer(buyer);
        User buyerAccount = new User();
        buyerAccount.setBuyer(savedBuyer);
        buyerAccount.setRawPassword(password);
        publicAccessPort.registerBuyerUser(buyerAccount);

        User sellerUser = login("seller@nexusmarket.com");
        Product laptop = sellerPort.registerProduct(sellerUser, laptop());
        sellerPort.publishProduct(sellerUser, laptop);
        Warehouse sellerWarehouse = sellerPort.consultMyWarehouses(sellerUser).get(0);
        for (ProductVariant variant : laptop.getVariants()) {
            sellerPort.registerInventoryInflow(sellerUser, inflow(variant, sellerWarehouse, 25));
        }
        LOGGER.info("Demonstration data loaded. Users: admin@, supervisor@, operator@, seller@, buyer@nexusmarket.com");
    }

    private User createFirstAdmin() {
        User admin = new User();
        admin.setIdentification("1000000001");
        admin.setName("Ana Admin");
        admin.setEmail("admin@nexusmarket.com");
        admin.setRole(SystemRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRawPassword(password);
        admin.setPasswordHash(passwordServicePort.encrypt(admin));
        admin.setRawPassword(null);
        admin.ensureRegistrationDataComplete();
        return userRepositoryPort.save(admin);
    }

    private User staff(String identification, String name, String email, SystemRole role) {
        User user = new User();
        user.setIdentification(identification);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setRawPassword(password);
        return user;
    }

    private User login(String email) {
        User credentials = new User();
        credentials.setEmail(email);
        credentials.setRawPassword(password);
        AuthenticationResult result = publicAccessPort.login(credentials);
        return result.getAuthenticatedUser();
    }

    private Product laptop() {
        Product product = new Product();
        product.setName("NexusBook 14 Laptop");
        product.setDescription("14-inch ultralight laptop.");
        product.setType(ProductType.PHYSICAL);
        product.setPrice(Money.of(new BigDecimal("3499000.00"), Currency.COP));
        product.setVariants(new ArrayList<>(List.of(variant("NB14-SLV-8", Map.of("color", "silver", "memory", "8GB")),
                variant("NB14-GRY-16", Map.of("color", "gray", "memory", "16GB")))));
        return product;
    }

    private ProductVariant variant(String sku, Map<String, String> attributes) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(sku);
        variant.setAttributes(new LinkedHashMap<>(attributes));
        return variant;
    }

    private InventoryMovement inflow(ProductVariant variant, Warehouse warehouse, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setVariant(variant);
        inventory.setWarehouse(warehouse);
        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setQuantity(quantity);
        movement.setNote("Initial stock");
        return movement;
    }
}
