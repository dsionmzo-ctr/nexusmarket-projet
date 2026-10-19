# Mapeo del proyecto de referencia (Banco) a NexusMarket

> **Para qué sirve.** El profesor entregó un proyecto de ejemplo (un banco) y las especificaciones de su `SDD/`. NexusMarket reutiliza **la misma arquitectura, las mismas convenciones y la misma estructura de documentos**, cambiando el contexto de negocio. Esta tabla permite estudiar el ejemplo y ver dónde reaparece cada idea en NexusMarket.

## 1. Idea → idea

| Concepto en el banco | Equivalente en NexusMarket | Comentario |
|---|---|---|
| `Customer` (`NaturalCustomer`, `BusinessCustomer`) | `Buyer` | Quien compra. NexusMarket tiene un solo tipo de comprador |
| `Person` (identificación, nombre, correo) | `Person` | Se reutiliza igual; también la extienden `Seller` y `User` |
| `User` (credenciales, rol, `authTokenVersion`) | `User` | Idéntico en concepto: sesión invalidada al subir la versión |
| `BankingProduct` (producto abstracto auditable) | `MarketplaceResource` | Base de todo recurso que puede ser sujeto de una `Operation` |
| `BankAccount` (saldo que nunca es negativo) | `Inventory` (stock que nunca es negativo) | Misma regla crítica: «no existencias/saldos negativos» |
| `Money` | `Money` | Igual (monto + moneda), con invariantes |
| `Loan` (EN ESTUDIO → APROBADO → DESEMBOLSADO) | `Order` (PENDIENTE DE PAGO → PAGADO → DESPACHADO → ENTREGADO) y `Refund` | El ciclo de vida es el proceso central |
| `Transfer` (ejecución, aprobación de alto monto) | `Payment` dentro de `Order`, `Shipment` y la decisión de `Refund` | La aprobación por un rol superior pasa a ser «el ADMIN decide el reembolso» |
| `Operation` + `AuditLog` (bitácora en MongoDB) | `Operation` + `AuditLog` | Idénticos |
| Roles de empleado (cajero, comercial, analista interno) | `LOGISTIC_OPERATOR`, `ADMIN`, `SUPERVISOR` | Empleados internos del marketplace |
| Roles de cliente (natural, empresa, operador, supervisor de empresa) | `BUYER`, `SELLER` | Participantes externos |
| Analista interno (único que aprueba préstamos) | `ADMIN` (único que decide reembolsos) | Un único rol con la decisión |
| `DomainCatalog` (catálogos como value objects) | `DomainCatalog` | Mismo patrón: `code`, `name`, `description`, `fromCode` |
| `UserStatus`, `AccountStatus`, `LoanStatus`, `TransferStatus`… | `UserStatus`, `OrderStatus`, `ShipmentStatus`, `RefundStatus`… | Un catálogo por concepto acotado |

## 2. Puertos de entrada

| Banco | NexusMarket |
|---|---|
| `PublicAccessPort` | `PublicAccessPort` |
| `NaturalCustomerPort`, `BusinessCustomerPort` | `BuyerPort` |
| `BusinessOperatorPort`, `BusinessSupervisorPort` | *(sin equivalente: NexusMarket no tiene usuarios delegados de empresa)* |
| `TellerEmployeePort` | `LogisticOperatorPort` (operación física) |
| `CommercialEmployeePort` | `SellerPort` (gestión comercial propia) |
| `InternalAnalystPort` | `AdminPort` |
| *(el analista tenía la consulta amplia)* | `SupervisorPort` (solo lectura) |

## 3. Puertos de salida

| Banco | NexusMarket |
|---|---|
| `CustomerRepositoryPort` | `BuyerRepositoryPort`, `SellerRepositoryPort` |
| `BankAccountRepositoryPort` | `InventoryRepositoryPort`, `InventoryMovementRepositoryPort` |
| `LoanRepositoryPort` | `OrderRepositoryPort`, `RefundRepositoryPort`, `ProductReturnRepositoryPort` |
| `TransferRepositoryPort` | `InvoiceRepositoryPort`, `ShipmentRepositoryPort` |
| `UserRepositoryPort`, `OperationRepositoryPort`, `AuditLogRepositoryPort` | iguales |
| `PasswordServicePort`, `JwtServicePort`, `AuthorizationPort`, `BusinessConfigurationPort`, `NotificationPort` | iguales |
| *(no existían)* | `WarehouseRepositoryPort`, `ProductRepositoryPort`, `CartRepositoryPort` |

## 4. Subdominios de servicios

| Banco (`domain/services/*.md`) | NexusMarket (`domain/services/*.md`) |
|---|---|
| `customer-services` | `buyer-services`, `seller-services` |
| `user-authentication-services` | `user-authentication-services` |
| `bank-account-services` | `inventory-services`, `warehouse-services`, `catalog-services` |
| `loan-services` | `order-services`, `cart-services`, `billing-services`, `shipping-services` |
| `transfer-services` | `returns-refunds-services` |
| `operation-audit-services` | `operation-audit-services` |
| `authorization-services` | `authorization-services` |
| *(no existían)* | `report-services`, `notification-services` |

## 5. Estructura de código

| Banco (`bank/`) | NexusMarket (`nexusmarket/`) |
|---|---|
| `src/main/java/application/domain/...` | igual |
| `adapters/rest`, `adapters/useCases`, `adapters/persistence`, `adapters/security`, `adapters/config`, `adapters/notification` | igual |
| `infrastructure/seed/DatabaseSeeder` | igual (+ `infrastructure/config/DomainServicesConfig`) |
| MySQL (`bank_db`, 3306) | **PostgreSQL** (`NexusMarket`, 5432) |
| Java 17 | **Java 21** |
| `spring.data.mongodb.uri` | `spring.mongodb.uri` (Spring Boot 4) |

## 6. Cosas que se mantuvieron *a propósito* igual

1. Arquitectura hexagonal con puertos de entrada **por rol**.
2. `UseCaseImpl` como capa transaccional que solo delega.
3. Servicios de dominio como clases planas registradas por `@ComponentScan` con filtro `.*Service`.
4. JWT con `sub`, `jti`, `ver`, `iat`, `exp` y usuario recargado de la BD en cada petición.
5. Sobre de error estándar con `X-Request-Id`; CORS de un solo origen; orden de validación.
6. Auditoría en MongoDB, inmutable.
7. Documentación `SDD/` con la misma partición (domain, Adapters, C4, Software Architecture, Orchestrator-prompt).

## 7. Cosas que cambian por el contexto de negocio

1. **Vendedores** que no se autorregistran y su **primera bodega**.
2. **Inventario distribuido** y **reservas** por pedido (con liberación al cancelar y salida por venta al pagar).
3. **Productos físicos y digitales**: los digitales no usan inventario ni envío.
4. **Vista restringida** del pedido para el vendedor (solo sus líneas).
5. **Devoluciones** (físicas, dentro de una ventana) con **reembolso** decidido por el administrador solo después de recibir la mercancía.
6. **Facturación** con impuesto y numeración consecutiva.
