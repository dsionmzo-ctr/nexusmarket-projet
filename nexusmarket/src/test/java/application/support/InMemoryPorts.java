package application.support;

import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.MarketplaceResource;
import application.domain.models.Notification;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.ProductReturn;
import application.domain.models.ProductVariant;
import application.domain.models.Refund;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.OperationRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ProductReturnRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * In-memory implementations of the output ports used by the domain tests. Every operation copies the
 * Domain Models (see {@link DeepCopy}) so that persistence semantics are the ones of a real database.
 */
public final class InMemoryPorts {

    private InMemoryPorts() {
    }

    /** Generic id-based store. */
    static class Store<T extends MarketplaceResource> {
        private final Map<Long, T> rows = new LinkedHashMap<>();
        private final AtomicLong sequence = new AtomicLong();

        T put(T value) {
            T copy = DeepCopy.of(value);
            if (copy.getId() == null) {
                copy.setId(sequence.incrementAndGet());
            }
            rows.put(copy.getId(), copy);
            return DeepCopy.of(copy);
        }

        Optional<T> get(Long id) {
            return id == null ? Optional.empty() : Optional.ofNullable(rows.get(id)).map(DeepCopy::of);
        }

        List<T> all() {
            return rows.values().stream().map(DeepCopy::of).toList();
        }

        List<T> where(Predicate<T> predicate) {
            return rows.values().stream().filter(predicate).map(DeepCopy::of).toList();
        }

        boolean any(Predicate<T> predicate) {
            return rows.values().stream().anyMatch(predicate);
        }
    }

    public static class Users implements UserRepositoryPort {
        private final Store<User> store = new Store<>();

        @Override
        public User save(User user) {
            return store.put(user);
        }

        @Override
        public Optional<User> findById(User user) {
            return store.get(user.getId());
        }

        @Override
        public Optional<User> findByEmail(User user) {
            return store.where(u -> u.getEmail().equalsIgnoreCase(user.getEmail())).stream().findFirst();
        }

        @Override
        public Optional<User> findByIdentification(User user) {
            return store.where(u -> u.getIdentification().equals(user.getIdentification())).stream().findFirst();
        }

        @Override
        public boolean existsByEmail(User user) {
            return store.any(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        }

        @Override
        public boolean existsByIdentification(User user) {
            return store.any(u -> u.getIdentification().equals(user.getIdentification()));
        }

        @Override
        public List<User> findAll() {
            return store.all();
        }

        @Override
        public void update(User user) {
            store.put(user);
        }

        @Override
        public User incrementAuthTokenVersion(User user) {
            User stored = store.get(user.getId()).orElseThrow();
            stored.bumpAuthTokenVersion();
            return store.put(stored);
        }
    }

    public static class Buyers implements BuyerRepositoryPort {
        private final Store<Buyer> store = new Store<>();

        @Override
        public Buyer save(Buyer buyer) {
            return store.put(buyer);
        }

        @Override
        public Optional<Buyer> findById(Buyer buyer) {
            return store.get(buyer.getId());
        }

        @Override
        public Optional<Buyer> findByIdentification(Buyer buyer) {
            return store.where(b -> b.getIdentification().equals(buyer.getIdentification())).stream().findFirst();
        }

        @Override
        public boolean existsByIdentification(Buyer buyer) {
            return store.any(b -> b.getIdentification().equals(buyer.getIdentification()));
        }

        @Override
        public boolean existsByEmail(Buyer buyer) {
            return store.any(b -> b.getEmail().equalsIgnoreCase(buyer.getEmail()));
        }

        @Override
        public List<Buyer> findAll() {
            return store.all();
        }

        @Override
        public void update(Buyer buyer) {
            store.put(buyer);
        }
    }

    public static class Sellers implements SellerRepositoryPort {
        private final Store<Seller> store = new Store<>();

        @Override
        public Seller save(Seller seller) {
            return store.put(seller);
        }

        @Override
        public Optional<Seller> findById(Seller seller) {
            return store.get(seller.getId());
        }

        @Override
        public Optional<Seller> findByIdentification(Seller seller) {
            return store.where(s -> s.getIdentification().equals(seller.getIdentification())).stream().findFirst();
        }

        @Override
        public boolean existsByIdentification(Seller seller) {
            return store.any(s -> s.getIdentification().equals(seller.getIdentification()));
        }

        @Override
        public boolean existsByEmail(Seller seller) {
            return store.any(s -> s.getEmail().equalsIgnoreCase(seller.getEmail()));
        }

        @Override
        public List<Seller> findAll() {
            return store.all();
        }

        @Override
        public void update(Seller seller) {
            store.put(seller);
        }
    }

    public static class Warehouses implements WarehouseRepositoryPort {
        private final Store<Warehouse> store = new Store<>();

        @Override
        public Warehouse save(Warehouse warehouse) {
            return store.put(warehouse);
        }

        @Override
        public Optional<Warehouse> findById(Warehouse warehouse) {
            return store.get(warehouse.getId());
        }

        @Override
        public List<Warehouse> findAll() {
            return store.all();
        }

        @Override
        public List<Warehouse> findBySeller(Seller seller) {
            return store.where(w -> w.getSeller() != null && w.getSeller().getId().equals(seller.getId()));
        }

        @Override
        public void update(Warehouse warehouse) {
            store.put(warehouse);
        }
    }

    public static class Products implements ProductRepositoryPort {
        private final Store<Product> store = new Store<>();
        private final AtomicLong variantSequence = new AtomicLong();

        @Override
        public Product save(Product product) {
            product.getVariants().stream().filter(v -> v.getId() == null).forEach(v -> v.setId(variantSequence.incrementAndGet()));
            return store.put(product);
        }

        @Override
        public Optional<Product> findById(Product product) {
            return store.get(product.getId());
        }

        @Override
        public Optional<Product> findByVariant(ProductVariant variant) {
            return store.where(p -> p.getVariants().stream().anyMatch(v -> v.getId().equals(variant.getId()))).stream().findFirst();
        }

        @Override
        public List<Product> findAll() {
            return store.all();
        }

        @Override
        public List<Product> findByStatus(Product product) {
            return store.where(p -> p.getStatus().equals(product.getStatus()));
        }

        @Override
        public List<Product> findBySeller(Seller seller) {
            return store.where(p -> p.getSeller().getId().equals(seller.getId()));
        }

        @Override
        public boolean existsVariantSku(ProductVariant variant) {
            return store.any(p -> p.getVariants().stream().anyMatch(v -> v.getSku().equals(variant.getSku())));
        }

        @Override
        public void update(Product product) {
            save(product);
        }
    }

    public static class Inventories implements InventoryRepositoryPort {
        private final Store<Inventory> store = new Store<>();

        @Override
        public Inventory save(Inventory inventory) {
            return store.put(inventory);
        }

        @Override
        public Optional<Inventory> findById(Inventory inventory) {
            return store.get(inventory.getId());
        }

        @Override
        public Optional<Inventory> findByVariantAndWarehouse(Inventory inventory) {
            return store.where(i -> i.getVariant().getId().equals(inventory.getVariant().getId())
                    && i.getWarehouse().getId().equals(inventory.getWarehouse().getId())).stream().findFirst();
        }

        @Override
        public List<Inventory> findByWarehouse(Warehouse warehouse) {
            return store.where(i -> i.getWarehouse().getId().equals(warehouse.getId()));
        }

        @Override
        public List<Inventory> findByVariant(ProductVariant variant) {
            return store.where(i -> i.getVariant().getId().equals(variant.getId()));
        }

        @Override
        public List<Inventory> findBySeller(Seller seller) {
            return store.where(i -> i.getProduct().getSeller().getId().equals(seller.getId()));
        }

        @Override
        public List<Inventory> findAll() {
            return store.all();
        }

        @Override
        public void update(Inventory inventory) {
            store.put(inventory);
        }
    }

    public static class Movements implements InventoryMovementRepositoryPort {
        private final List<InventoryMovement> rows = new ArrayList<>();
        private final AtomicLong sequence = new AtomicLong();

        @Override
        public InventoryMovement save(InventoryMovement movement) {
            InventoryMovement copy = DeepCopy.of(movement);
            copy.setId(sequence.incrementAndGet());
            rows.add(copy);
            return DeepCopy.of(copy);
        }

        @Override
        public List<InventoryMovement> findByInventory(Inventory inventory) {
            return rows.stream().filter(m -> m.getInventory().getId().equals(inventory.getId())).map(DeepCopy::of).toList();
        }
    }

    public static class Carts implements CartRepositoryPort {
        private final Map<Long, Cart> rows = new LinkedHashMap<>();
        private final AtomicLong sequence = new AtomicLong();

        @Override
        public Cart save(Cart cart) {
            Cart copy = DeepCopy.of(cart);
            if (copy.getId() == null) {
                copy.setId(sequence.incrementAndGet());
            }
            rows.put(copy.getId(), copy);
            return DeepCopy.of(copy);
        }

        @Override
        public Optional<Cart> findByBuyer(Buyer buyer) {
            return rows.values().stream().filter(c -> c.getBuyer().getId().equals(buyer.getId())).findFirst().map(DeepCopy::of);
        }
    }

    public static class Orders implements OrderRepositoryPort {
        private final Store<Order> store = new Store<>();
        private final AtomicLong itemSequence = new AtomicLong();

        @Override
        public Order save(Order order) {
            for (OrderItem item : order.getItems()) {
                if (item.getId() == null) {
                    item.setId(itemSequence.incrementAndGet());
                }
            }
            return store.put(order);
        }

        @Override
        public Optional<Order> findById(Order order) {
            return store.get(order.getId());
        }

        @Override
        public List<Order> findByBuyer(Buyer buyer) {
            return store.where(o -> o.getBuyer().getId().equals(buyer.getId()));
        }

        @Override
        public List<Order> findBySeller(Seller seller) {
            return store.where(o -> o.getItems().stream().anyMatch(i -> i.getSeller().getId().equals(seller.getId())));
        }

        @Override
        public List<Order> findAll() {
            return store.all();
        }

        @Override
        public void update(Order order) {
            save(order);
        }
    }

    public static class Invoices implements InvoiceRepositoryPort {
        private final Store<Invoice> store = new Store<>();
        private final AtomicLong numbers = new AtomicLong();

        @Override
        public Invoice save(Invoice invoice) {
            return store.put(invoice);
        }

        @Override
        public Optional<Invoice> findByOrder(Order order) {
            return store.where(i -> i.getOrder().getId().equals(order.getId())).stream().findFirst();
        }

        @Override
        public String nextInvoiceNumber() {
            return String.format("INV-%08d", numbers.incrementAndGet());
        }
    }

    public static class Shipments implements ShipmentRepositoryPort {
        private final Store<Shipment> store = new Store<>();

        @Override
        public Shipment save(Shipment shipment) {
            return store.put(shipment);
        }

        @Override
        public Optional<Shipment> findById(Shipment shipment) {
            return store.get(shipment.getId());
        }

        @Override
        public Optional<Shipment> findByOrder(Order order) {
            return store.where(s -> s.getOrder().getId().equals(order.getId())).stream().findFirst();
        }

        @Override
        public List<Shipment> findByStatus(Shipment shipment) {
            return shipment.getStatus() == null ? store.all() : store.where(s -> s.getStatus().equals(shipment.getStatus()));
        }

        @Override
        public void update(Shipment shipment) {
            store.put(shipment);
        }
    }

    public static class Returns implements ProductReturnRepositoryPort {
        private final Store<ProductReturn> store = new Store<>();

        @Override
        public ProductReturn save(ProductReturn productReturn) {
            return store.put(productReturn);
        }

        @Override
        public Optional<ProductReturn> findById(ProductReturn productReturn) {
            return store.get(productReturn.getId());
        }

        @Override
        public List<ProductReturn> findByOrder(Order order) {
            return store.where(r -> r.getOrder().getId().equals(order.getId()));
        }

        @Override
        public List<ProductReturn> findByBuyer(Buyer buyer) {
            return store.where(r -> r.getOrder().getBuyer().getId().equals(buyer.getId()));
        }

        @Override
        public List<ProductReturn> findByStatus(ProductReturn productReturn) {
            return productReturn.getStatus() == null ? store.all()
                    : store.where(r -> r.getStatus().equals(productReturn.getStatus()));
        }

        @Override
        public void update(ProductReturn productReturn) {
            store.put(productReturn);
        }
    }

    public static class Refunds implements RefundRepositoryPort {
        private final Store<Refund> store = new Store<>();

        @Override
        public Refund save(Refund refund) {
            return store.put(refund);
        }

        @Override
        public Optional<Refund> findById(Refund refund) {
            return store.get(refund.getId());
        }

        @Override
        public Optional<Refund> findByReturn(ProductReturn productReturn) {
            return store.where(r -> r.getProductReturn().getId().equals(productReturn.getId())).stream().findFirst();
        }

        @Override
        public List<Refund> findByBuyer(Buyer buyer) {
            return store.where(r -> r.getProductReturn().getOrder().getBuyer().getId().equals(buyer.getId()));
        }

        @Override
        public List<Refund> findByStatus(Refund refund) {
            return refund.getStatus() == null ? store.all() : store.where(r -> r.getStatus().equals(refund.getStatus()));
        }

        @Override
        public void update(Refund refund) {
            store.put(refund);
        }
    }

    public static class Operations implements OperationRepositoryPort {
        private final List<Operation> rows = new ArrayList<>();
        private final AtomicLong sequence = new AtomicLong();

        @Override
        public Operation save(Operation operation) {
            Operation copy = DeepCopy.of(operation);
            copy.setOperationId(sequence.incrementAndGet());
            rows.add(copy);
            return DeepCopy.of(copy);
        }

        @Override
        public Optional<Operation> findById(Operation operation) {
            return rows.stream().filter(o -> o.getOperationId().equals(operation.getOperationId())).findFirst().map(DeepCopy::of);
        }

        @Override
        public List<Operation> findByUser(User user) {
            return rows.stream().filter(o -> o.getPerformedBy().getId().equals(user.getId())).map(DeepCopy::of).toList();
        }

        @Override
        public List<Operation> findByProduct(MarketplaceResource resource) {
            return rows.stream().filter(o -> o.getAffectedResource().getResourceType().equals(resource.getResourceType())
                    && o.getAffectedResource().getId().equals(resource.getId())).map(DeepCopy::of).toList();
        }

        @Override
        public List<Operation> findByType(Operation operation) {
            return rows.stream().filter(o -> o.getOperationType().equals(operation.getOperationType())).map(DeepCopy::of).toList();
        }

        @Override
        public List<Operation> findAll() {
            return rows.stream().map(DeepCopy::of).toList();
        }
    }

    public static class AuditLogs implements AuditLogRepositoryPort {
        private final List<AuditLog> rows = new ArrayList<>();
        private final AtomicLong sequence = new AtomicLong();

        @Override
        public AuditLog save(AuditLog auditLog) {
            AuditLog copy = DeepCopy.of(auditLog);
            copy.setAuditId("audit-" + sequence.incrementAndGet());
            rows.add(copy);
            return DeepCopy.of(copy);
        }

        @Override
        public List<AuditLog> findByUser(User user) {
            return rows.stream().filter(a -> a.getPerformedBy().getId().equals(user.getId())).map(DeepCopy::of).toList();
        }

        @Override
        public List<AuditLog> findByProduct(MarketplaceResource resource) {
            return rows.stream().filter(a -> a.getAffectedResource().getResourceType().equals(resource.getResourceType())
                    && a.getAffectedResource().getId().equals(resource.getId())).map(DeepCopy::of).toList();
        }

        @Override
        public List<AuditLog> findByOperationType(AuditLog auditLog) {
            return rows.stream().filter(a -> a.getOperationType().equals(auditLog.getOperationType())).map(DeepCopy::of).toList();
        }

        @Override
        public List<AuditLog> findAll() {
            return rows.stream().map(DeepCopy::of).toList();
        }
    }

    public static class Notifications implements NotificationPort {
        public final List<Notification> sent = new ArrayList<>();

        @Override
        public void send(Notification notification) {
            sent.add(notification);
        }
    }
}
