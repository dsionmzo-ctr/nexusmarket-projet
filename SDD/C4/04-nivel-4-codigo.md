# C4 Nivel 4 — Código

## Propósito

Mostrar las clases clave y sus colaboraciones. Es un recorte: solo lo que participa en los flujos principales, no el 100 % del código.

## Dominio: pedido, inventario y envío

```mermaid
classDiagram
    class Order {
      +place(buyer, items, address, now)$ Order
      +total() Money
      +markPaid(method, now)
      +markShipped()
      +markDelivered(now)
      +cancel()
      +forSeller(seller) Order
    }
    class OrderItem
    class StockAllocation
    class Inventory {
      +available() int
      +reserve(q)
      +releaseReservation(q)
      +confirmSale(q)
      +receive(q)
      +registerReturn(q)
      +adjustTo(n)
      +markDamaged()
      +markAvailable()
    }
    class Shipment {
      +prepareFor(order, tracking, now)$ Shipment
      +dispatch(operator, now)
      +confirmDelivery(operator, now)
    }
    class Invoice
    class ProductReturn {
      +request(...)$ ProductReturn
      +receive(warehouse, now)
      +reject()
    }
    class Refund {
      +request(return, amount, now)$ Refund
      +approve(admin, note, now)
      +reject(admin, note, now)
    }
    Order "1" *-- "1..*" OrderItem
    OrderItem "1" *-- "0..*" StockAllocation
    StockAllocation --> Inventory
    Order "1" --> "0..1" Invoice
    Order "1" --> "0..1" Shipment
    Order "1" --> "0..*" ProductReturn
    ProductReturn "1" --> "1" Refund
```

## Servicios del flujo de compra

```mermaid
classDiagram
    class BuyerUseCaseImpl
    class CheckoutService
    class ReserveStockService
    class PayOrderService
    class ConfirmStockSaleService
    class GenerateInvoiceService
    class CreateShipmentService
    class RegisterOperationAndAuditService
    class AuthorizeOperationService
    BuyerUseCaseImpl --> CheckoutService
    BuyerUseCaseImpl --> PayOrderService
    CheckoutService --> ReserveStockService
    CheckoutService --> AuthorizeOperationService
    PayOrderService --> ConfirmStockSaleService
    PayOrderService --> GenerateInvoiceService
    PayOrderService --> CreateShipmentService
    CheckoutService --> RegisterOperationAndAuditService
    PayOrderService --> RegisterOperationAndAuditService
```

## Autenticación

```mermaid
classDiagram
    class JwtAuthenticationFilter
    class JwtProvider
    class SecurityUserLoader
    class LoadAuthenticatedUserService
    class AuthenticatedUserPrincipal {
      +domainUser() User
    }
    class LoginService
    class PasswordServicePort
    class JwtServicePort
    JwtProvider ..|> JwtServicePort
    BCryptPasswordServiceAdapter ..|> PasswordServicePort
    JwtAuthenticationFilter --> JwtProvider
    JwtAuthenticationFilter --> SecurityUserLoader
    SecurityUserLoader --> LoadAuthenticatedUserService
    SecurityUserLoader --> AuthenticatedUserPrincipal
    LoginService --> PasswordServicePort
    LoginService --> JwtServicePort
```

## Persistencia (patrón repetido en los 14 repositorios JPA)

```mermaid
classDiagram
    class OrderRepositoryPort {
      <<interface>>
      +save(order) Order
      +findById(order) Optional~Order~
      +findByBuyer(buyer) List~Order~
    }
    class OrderJpaAdapter
    class OrderJpaRepository {
      <<Spring Data>>
    }
    class OrderJpaMapper {
      +toEntity(order)$
      +toDomain(entity)$
    }
    class OrderJpaEntity
    OrderJpaAdapter ..|> OrderRepositoryPort
    OrderJpaAdapter --> OrderJpaRepository
    OrderJpaAdapter --> OrderJpaMapper
    OrderJpaMapper --> OrderJpaEntity
```
