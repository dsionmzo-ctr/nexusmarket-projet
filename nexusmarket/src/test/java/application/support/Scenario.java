package application.support;

import application.adapters.useCases.AdminUseCaseImpl;
import application.adapters.useCases.BuyerUseCaseImpl;
import application.adapters.useCases.LogisticOperatorUseCaseImpl;
import application.adapters.useCases.PublicAccessUseCaseImpl;
import application.adapters.useCases.SellerUseCaseImpl;
import application.adapters.useCases.SupervisorUseCaseImpl;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductVariant;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.models.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Builds the typical state of the marketplace through the input ports (admin, staff, seller with its
 * warehouse, buyer) so that every test starts from a realistic situation.
 */
public class Scenario {

    public static final String PASSWORD = "Password123*";

    public final InMemoryContainer container = new InMemoryContainer();
    public final PublicAccessUseCaseImpl publicAccess = container.get(PublicAccessUseCaseImpl.class);
    public final BuyerUseCaseImpl buyerPort = container.get(BuyerUseCaseImpl.class);
    public final SellerUseCaseImpl sellerPort = container.get(SellerUseCaseImpl.class);
    public final LogisticOperatorUseCaseImpl logisticPort = container.get(LogisticOperatorUseCaseImpl.class);
    public final AdminUseCaseImpl adminPort = container.get(AdminUseCaseImpl.class);
    public final SupervisorUseCaseImpl supervisorPort = container.get(SupervisorUseCaseImpl.class);

    public User admin;
    public User supervisor;
    public User operator;
    public User sellerUser;
    public User otherSellerUser;
    public User buyerUser;
    public Warehouse sellerWarehouse;
    public Warehouse marketplaceWarehouse;

    public Scenario() {
        admin = createFirstAdmin();
        supervisor = adminPort.registerStaffUser(admin, staff("1000000002", "Sofia Supervisor", "supervisor@nexus.com", SystemRole.SUPERVISOR));
        operator = adminPort.registerStaffUser(admin, staff("1000000003", "Luis Logistica", "operator@nexus.com", SystemRole.LOGISTIC_OPERATOR));
        marketplaceWarehouse = adminPort.createWarehouse(admin, warehouse("Marketplace Warehouse", "MARKETPLACE"));
        sellerUser = incorporateSeller("900111111", "Tech Store SAS", "seller@nexus.com", "Tech Store");
        otherSellerUser = incorporateSeller("900222222", "Home Goods SAS", "other.seller@nexus.com", "Home Goods");
        sellerWarehouse = sellerPort.consultMyWarehouses(sellerUser).get(0);
        buyerUser = registerBuyer("1017123456", "Juan Perez", "buyer@nexus.com");
    }

    private User createFirstAdmin() {
        User user = new User();
        user.setIdentification("1000000001");
        user.setName("Ana Admin");
        user.setEmail("admin@nexus.com");
        user.setRole(SystemRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setRawPassword(PASSWORD);
        user.setPasswordHash(container.get(application.domain.ports.out.PasswordServicePort.class).encrypt(user));
        user.setRawPassword(null);
        return container.users.save(user);
    }

    public static User staff(String identification, String name, String email, SystemRole role) {
        User user = new User();
        user.setIdentification(identification);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setRawPassword(PASSWORD);
        return user;
    }

    public static Warehouse warehouse(String name, String type) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setType(application.domain.valueobjects.WarehouseType.fromCode(type));
        warehouse.setAddress(new Address("Calle 1 # 2-3", "Medellin", "Colombia"));
        return warehouse;
    }

    public User incorporateSeller(String identification, String name, String email, String businessName) {
        Seller seller = new Seller();
        seller.setIdentification(identification);
        seller.setName(name);
        seller.setEmail(email);
        seller.setBusinessName(businessName);
        Warehouse first = new Warehouse();
        first.setName(businessName + " Warehouse");
        first.setAddress(new Address("Carrera 43A # 1-50", "Medellin", "Colombia"));
        Seller saved = adminPort.registerSeller(admin, seller, first);
        User account = new User();
        Seller reference = new Seller();
        reference.setIdentification(saved.getIdentification());
        account.setSeller(reference);
        account.setRawPassword(PASSWORD);
        return adminPort.registerSellerUser(admin, account);
    }

    public User registerBuyer(String identification, String name, String email) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(identification);
        buyer.setName(name);
        buyer.setEmail(email);
        buyer.setPrimaryAddress(new Address("Calle 50 # 40-20", "Medellin", "Colombia"));
        Buyer saved = publicAccess.registerBuyer(buyer);
        User account = new User();
        Buyer reference = new Buyer();
        reference.setIdentification(saved.getIdentification());
        account.setBuyer(reference);
        account.setRawPassword(PASSWORD);
        return publicAccess.registerBuyerUser(account);
    }

    public static Product product(String name, ProductType type, String price, String... skus) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(name + " description");
        product.setType(type);
        product.setPrice(Money.of(new BigDecimal(price), Currency.COP));
        List<ProductVariant> variants = new ArrayList<>();
        for (String sku : skus) {
            ProductVariant variant = new ProductVariant();
            variant.setSku(sku);
            variant.setAttributes(new LinkedHashMap<>());
            variants.add(variant);
        }
        product.setVariants(variants);
        return product;
    }

    /** Registers and publishes a physical product of the seller and stocks every variant in its warehouse. */
    public Product publishedPhysicalProduct(User seller, Warehouse warehouse, String name, String price, int stock, String... skus) {
        Product registered = sellerPort.registerProduct(seller, product(name, ProductType.PHYSICAL, price, skus));
        Product published = sellerPort.publishProduct(seller, registered);
        for (ProductVariant variant : published.getVariants()) {
            sellerPort.registerInventoryInflow(seller, inflow(variant, warehouse, stock, "initial stock"));
        }
        return published;
    }

    public Product publishedDigitalProduct(User seller, String name, String price, String sku) {
        Product registered = sellerPort.registerProduct(seller, product(name, ProductType.DIGITAL, price, sku));
        return sellerPort.publishProduct(seller, registered);
    }

    public static InventoryMovement inflow(ProductVariant variant, Warehouse warehouse, int quantity, String note) {
        Inventory inventory = new Inventory();
        ProductVariant variantRef = new ProductVariant();
        variantRef.setId(variant.getId());
        inventory.setVariant(variantRef);
        Warehouse warehouseRef = new Warehouse();
        warehouseRef.setId(warehouse.getId());
        inventory.setWarehouse(warehouseRef);
        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setQuantity(quantity);
        movement.setNote(note);
        return movement;
    }

    public static CartItem cartItem(ProductVariant variant, int quantity) {
        CartItem item = new CartItem();
        ProductVariant ref = new ProductVariant();
        ref.setId(variant.getId());
        item.setVariant(ref);
        item.setQuantity(quantity);
        return item;
    }

    public static Order orderRef(Long id) {
        Order order = new Order();
        order.setId(id);
        return order;
    }

    public static Order payment(Long id, PaymentMethod method) {
        Order order = orderRef(id);
        order.setPaymentMethod(method);
        return order;
    }

    public static User credentials(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setRawPassword(password);
        return user;
    }
}
