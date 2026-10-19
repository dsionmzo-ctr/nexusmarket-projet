# Puertos de salida

## Introducción

Los **puertos de salida** (`application.domain.ports.out`) son las interfaces que el dominio necesita del mundo exterior: persistencia, cifrado, token, configuración y notificaciones. El dominio solo los conoce como interfaces; los **adaptadores** los implementan con la tecnología concreta (JPA, MongoDB, BCrypt, JJWT…).

Reglas comunes:

- Reciben y devuelven **modelos de dominio**, nunca entidades JPA, documentos Mongo ni DTOs.
- Las consultas por referencia usan un modelo «sonda» (`findById(Order probe)`) en lugar de ids sueltos.
- Los repositorios devuelven `Optional` cuando el resultado puede no existir; el servicio decide si eso es `EntityNotFoundException`.

| Puerto | Responsabilidad | Adaptador | Paquete |
|---|---|---|---|
| `AuditLogRepositoryPort` | Persistencia de registros de auditoría inmutables | `AuditLogMongoAdapter` | `adapters.persistence.mongodb.adapters` |
| `AuthorizationPort` | Información de autorización externa, reservada para decisiones que no pueden tomarse con los modelos de dominio disponibles | `RoleAuthorizationAdapter` | `adapters.security` |
| `BusinessConfigurationPort` | Parámetros de negocio configurables externamente (tasa de impuesto, ventana de devolución) | `DefaultBusinessConfigurationAdapter` | `adapters.config` |
| `BuyerRepositoryPort` | Persistencia y consulta de compradores | `BuyerJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `CartRepositoryPort` | Persistencia de los carritos de los compradores | `CartJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `InventoryMovementRepositoryPort` | Libro de movimientos de inventario, solo-agregar | `InventoryMovementJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `InventoryRepositoryPort` | Persistencia y consulta del inventario distribuido | `InventoryJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `InvoiceRepositoryPort` | Persistencia de facturas (inmutables una vez emitidas) y numeración | `InvoiceJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `JwtServicePort` | Generación del token de acceso | `JwtProvider` | `adapters.security` |
| `NotificationPort` | Comunicación con sistemas externos de notificación (correo, SMS, push) | `NoOpNotificationAdapter` | `adapters.notification` |
| `OperationRepositoryPort` | Persistencia de las `Operation` de negocio (base relacional) | `OperationJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `OrderRepositoryPort` | Persistencia y consulta de pedidos | `OrderJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `PasswordServicePort` | Cifrado y verificación de contraseñas, abstraído de cualquier algoritmo concreto | `BCryptPasswordServiceAdapter` | `adapters.security` |
| `ProductRepositoryPort` | Persistencia y consulta de productos del catálogo y sus variantes | `ProductJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `ProductReturnRepositoryPort` | Persistencia y consulta de devoluciones | `ProductReturnJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `RefundRepositoryPort` | Persistencia y consulta de reembolsos | `RefundJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `SellerRepositoryPort` | Persistencia y consulta de vendedores | `SellerJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `ShipmentRepositoryPort` | Persistencia y consulta de envíos | `ShipmentJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `UserRepositoryPort` | Persistencia y consulta de usuarios del sistema | `UserJpaAdapter` | `adapters.persistence.jpa.adapters` |
| `WarehouseRepositoryPort` | Persistencia y consulta de bodegas | `WarehouseJpaAdapter` | `adapters.persistence.jpa.adapters` |

---

## AuditLogRepositoryPort

Persistencia de registros de auditoría inmutables. Se espera MongoDB; el dominio lo desconoce por completo.

Implementado por `AuditLogMongoAdapter` (`adapters.persistence.mongodb.adapters`).

```java
public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByProduct(MarketplaceResource resource);
    List<AuditLog> findByOperationType(AuditLog auditLog);
    List<AuditLog> findAll();
}
```

---

## AuthorizationPort

Información de autorización externa, reservada para decisiones que no pueden tomarse con los modelos de dominio disponibles. No reemplaza la validación de dominio: las reglas que dependen solo del rol las evalúan los servicios de autorización.

Implementado por `RoleAuthorizationAdapter` (`adapters.security`).

```java
public interface AuthorizationPort {
    boolean isAuthorized(User user, User target);
    boolean canOperateOn(User user, MarketplaceResource resource);
    boolean canApprove(User user, MarketplaceResource resource);
}
```

---

## BusinessConfigurationPort

Parámetros de negocio configurables externamente (tasa de impuesto, ventana de devolución).

Implementado por `DefaultBusinessConfigurationAdapter` (`adapters.config`).

```java
public interface BusinessConfigurationPort {
    BigDecimal getTaxRate();
    Integer getReturnWindowDays();
}
```

---

## BuyerRepositoryPort

Persistencia y consulta de compradores.

Implementado por `BuyerJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface BuyerRepositoryPort {
    Buyer save(Buyer buyer);
    Optional<Buyer> findById(Buyer buyer);
    Optional<Buyer> findByIdentification(Buyer buyer);
    boolean existsByIdentification(Buyer buyer);
    boolean existsByEmail(Buyer buyer);
    List<Buyer> findAll();
    void update(Buyer buyer);
}
```

---

## CartRepositoryPort

Persistencia de los carritos de los compradores.

Implementado por `CartJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findByBuyer(Buyer buyer);
}
```

---

## InventoryMovementRepositoryPort

Libro de movimientos de inventario, solo-agregar.

Implementado por `InventoryMovementJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface InventoryMovementRepositoryPort {
    InventoryMovement save(InventoryMovement movement);
    List<InventoryMovement> findByInventory(Inventory inventory);
}
```

---

## InventoryRepositoryPort

Persistencia y consulta del inventario distribuido.

Implementado por `InventoryJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(Inventory inventory);
    Optional<Inventory> findByVariantAndWarehouse(Inventory inventory);
    List<Inventory> findByWarehouse(Warehouse warehouse);
    List<Inventory> findByVariant(ProductVariant variant);
    List<Inventory> findBySeller(Seller seller);
    List<Inventory> findAll();
    void update(Inventory inventory);
}
```

---

## InvoiceRepositoryPort

Persistencia de facturas (inmutables una vez emitidas) y numeración.

Implementado por `InvoiceJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findByOrder(Order order);
    String nextInvoiceNumber();
}
```

---

## JwtServicePort

Generación del token de acceso. El dominio no depende de librerías JWT. El token nunca contiene la contraseña del usuario.

Implementado por `JwtProvider` (`adapters.security`).

```java
public interface JwtServicePort {
    String generateToken(User user);
    long getExpirationSeconds();
}
```

---

## NotificationPort

Comunicación con sistemas externos de notificación (correo, SMS, push).

Implementado por `NoOpNotificationAdapter` (`adapters.notification`).

```java
public interface NotificationPort {
    void send(Notification notification);
}
```

---

## OperationRepositoryPort

Persistencia de las `Operation` de negocio (base relacional).

Implementado por `OperationJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface OperationRepositoryPort {
    Operation save(Operation operation);
    Optional<Operation> findById(Operation operation);
    List<Operation> findByUser(User user);
    List<Operation> findByProduct(MarketplaceResource resource);
    List<Operation> findByType(Operation operation);
    List<Operation> findAll();
}
```

---

## OrderRepositoryPort

Persistencia y consulta de pedidos.

Implementado por `OrderJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Order order);
    List<Order> findByBuyer(Buyer buyer);
    List<Order> findBySeller(Seller seller);
    List<Order> findAll();
    void update(Order order);
}
```

---

## PasswordServicePort

Cifrado y verificación de contraseñas, abstraído de cualquier algoritmo concreto.

Implementado por `BCryptPasswordServiceAdapter` (`adapters.security`).

```java
public interface PasswordServicePort {
    boolean matches(User user);
    String encrypt(User user);
}
```

---

## ProductRepositoryPort

Persistencia y consulta de productos del catálogo y sus variantes.

Implementado por `ProductJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Product product);
    Optional<Product> findByVariant(ProductVariant variant);
    List<Product> findAll();
    List<Product> findByStatus(Product product);
    List<Product> findBySeller(Seller seller);
    boolean existsVariantSku(ProductVariant variant);
    void update(Product product);
}
```

---

## ProductReturnRepositoryPort

Persistencia y consulta de devoluciones.

Implementado por `ProductReturnJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface ProductReturnRepositoryPort {
    ProductReturn save(ProductReturn productReturn);
    Optional<ProductReturn> findById(ProductReturn productReturn);
    List<ProductReturn> findByOrder(Order order);
    List<ProductReturn> findByBuyer(Buyer buyer);
    List<ProductReturn> findByStatus(ProductReturn productReturn);
    void update(ProductReturn productReturn);
}
```

---

## RefundRepositoryPort

Persistencia y consulta de reembolsos.

Implementado por `RefundJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface RefundRepositoryPort {
    Refund save(Refund refund);
    Optional<Refund> findById(Refund refund);
    Optional<Refund> findByReturn(ProductReturn productReturn);
    List<Refund> findByBuyer(Buyer buyer);
    List<Refund> findByStatus(Refund refund);
    void update(Refund refund);
}
```

---

## SellerRepositoryPort

Persistencia y consulta de vendedores.

Implementado por `SellerJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface SellerRepositoryPort {
    Seller save(Seller seller);
    Optional<Seller> findById(Seller seller);
    Optional<Seller> findByIdentification(Seller seller);
    boolean existsByIdentification(Seller seller);
    boolean existsByEmail(Seller seller);
    List<Seller> findAll();
    void update(Seller seller);
}
```

---

## ShipmentRepositoryPort

Persistencia y consulta de envíos.

Implementado por `ShipmentJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface ShipmentRepositoryPort {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(Shipment shipment);
    Optional<Shipment> findByOrder(Order order);
    List<Shipment> findByStatus(Shipment shipment);
    void update(Shipment shipment);
}
```

---

## UserRepositoryPort

Persistencia y consulta de usuarios del sistema. Trabaja solo con modelos de dominio.

Implementado por `UserJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(User user);
    Optional<User> findByEmail(User user);
    Optional<User> findByIdentification(User user);
    boolean existsByEmail(User user);
    boolean existsByIdentification(User user);
    List<User> findAll();
    void update(User user);
    User incrementAuthTokenVersion(User user);
}
```

---

## WarehouseRepositoryPort

Persistencia y consulta de bodegas.

Implementado por `WarehouseJpaAdapter` (`adapters.persistence.jpa.adapters`).

```java
public interface WarehouseRepositoryPort {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(Warehouse warehouse);
    List<Warehouse> findAll();
    List<Warehouse> findBySeller(Seller seller);
    void update(Warehouse warehouse);
}
```

---
