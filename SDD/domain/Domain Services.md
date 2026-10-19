# Servicios del dominio

## Introducción

Este documento ofrece la visión conceptual de los **105 servicios de dominio** que componen NexusMarket. Cada servicio implementa **un único caso de uso** o una única regla reutilizable (Principio de Responsabilidad Única), de modo que cada uno es fácil de justificar, de probar y de reutilizar.

La definición detallada de cada servicio (firma, roles, colaboradores, puertos, excepciones y operaciones auditadas) está en archivos separados por **subdominio**, en `SDD/domain/services/`.

## Subdominios

| Subdominio | Documento | Servicios |
|---|---|---|
| Autorización | [`authorization-services.md`](services/authorization-services.md) | 9 |
| Facturación | [`billing-services.md`](services/billing-services.md) | 2 |
| Compradores | [`buyer-services.md`](services/buyer-services.md) | 6 |
| Carrito | [`cart-services.md`](services/cart-services.md) | 6 |
| Catálogo | [`catalog-services.md`](services/catalog-services.md) | 12 |
| Inventario | [`inventory-services.md`](services/inventory-services.md) | 13 |
| Notificaciones | [`notification-services.md`](services/notification-services.md) | 1 |
| Operaciones y auditoría | [`operation-audit-services.md`](services/operation-audit-services.md) | 9 |
| Pedidos | [`order-services.md`](services/order-services.md) | 10 |
| Reportes administrativos | [`report-services.md`](services/report-services.md) | 1 |
| Devoluciones y reembolsos | [`returns-refunds-services.md`](services/returns-refunds-services.md) | 12 |
| Vendedores | [`seller-services.md`](services/seller-services.md) | 3 |
| Envíos | [`shipping-services.md`](services/shipping-services.md) | 6 |
| Usuarios y autenticación | [`user-authentication-services.md`](services/user-authentication-services.md) | 9 |
| Bodegas | [`warehouse-services.md`](services/warehouse-services.md) | 6 |

## Tipos de servicio

| Tipo | Convención de nombre | Ejemplos |
|---|---|---|
| Caso de uso | verbo + objeto + `Service` | `CheckoutService`, `PayOrderService`, `RegisterProductService` |
| Consulta | `Consult…Service` | `ConsultCartService`, `ConsultRefundsService` |
| Búsqueda / carga | `Find…Service`, `Resolve…Service` | `FindOrderService`, `ResolveAuthenticatedBuyerService` |
| Regla de autorización | `Authorize…`, `Validate…` | `AuthorizeOperationService`, `ValidateSellerOwnershipService` |
| Trazabilidad | `Register…Operation…`, `RegisterAuditLogService` | `RegisterOperationAndAuditService` |
| Colaboración técnica del dominio | `Notify…Service`, `Record…Service` | `NotifyPersonService`, `RecordInventoryMovementService` |

## Reglas comunes

- Todo servicio de negocio comienza autorizando al usuario con `AuthorizeOperationService` (RG-01, RG-02, RG-03) salvo los públicos (`RegisterBuyerService`, `LoginService`, catálogo público).
- Todo servicio que modifica el estado de negocio registra su trazabilidad con `RegisterOperationAndAuditService`.
- Los servicios son clases Java planas: el contenedor las registra mediante `DomainServicesConfig` (`@ComponentScan` con el filtro `.*Service`), sin acoplar el dominio a Spring.
- El reloj (`Clock`) se inyecta; los servicios nunca llaman a `LocalDateTime.now()` sin él.
- Los servicios reciben modelos de dominio: nunca `String role`, `Long userId` sueltos (la única excepción documentada es `LoadAuthenticatedUserService`, cuyo insumo es el contenido de un token).
