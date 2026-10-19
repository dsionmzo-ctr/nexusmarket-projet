# Excepciones de dominio

## Introducción

El dominio comunica las violaciones de reglas de negocio con **excepciones propias** (todas heredan de `DomainException`, sin dependencias de framework). Cada excepción tiene **significado de negocio**; el adaptador REST (`ErrorCodeMapping` + `GlobalExceptionHandler`) las traduce a un código HTTP y a un `code` estable. Ver `Adapters/Global-exception-handler.md`.

Hay **21** excepciones.

| Excepción | Cuándo se lanza | HTTP / `code` | Lanzada por (modelos) | Nº de servicios que la lanzan |
|---|---|---|---|---|
| `DamagedInventoryException` | Se intentó reservar inventario marcado como dañado (validación crítica de inventario). | 409 `DAMAGED_INVENTORY` | `Inventory` | 0 |
| `DomainException` | Base de toda violación de regla de negocio levantada por el dominio. | 400 `INVALID_REQUEST` (por defecto) | `Address`, `Buyer`, `Invoice`, `Money`, `StockAllocation` | 2 |
| `DuplicateResourceException` | Un valor único (correo, identificación, SKU…) ya existe. | 409 `RESOURCE_ALREADY_EXISTS` | `Product` | 7 |
| `EntityNotFoundException` | La entidad referenciada no existe o no es visible para el usuario. | 404 `RESOURCE_NOT_FOUND` | — | 24 |
| `ImmutableOrderException` | Se intentó modificar un pedido finalizado (los pedidos finalizados son inmutables). | 409 `INVALID_STATE_TRANSITION` | `Order` | 0 |
| `InsufficientStockException` | No hay stock disponible suficiente; nunca se permiten existencias negativas. | 409 `INSUFFICIENT_STOCK` | `Inventory` | 1 |
| `InvalidAuditLogException` | Un registro `AuditLog` está incompleto o es inválido. | 500 `INTERNAL_ERROR` (lo construyen los servicios) | `AuditLog` | 1 |
| `InvalidCartException` | Violación de una regla del agregado carrito. | 400 `INVALID_CART` | `Cart`, `CartItem` | 1 |
| `InvalidCatalogCodeException` | Un código de catálogo no está soportado (fábricas `fromCode` validadas). | 400 `INVALID_REQUEST` | `DomainCatalog` | 0 |
| `InvalidCredentialsException` | El par correo/contraseña es incorrecto (mismo mensaje en ambos casos). | 401 `INVALID_CREDENTIALS` | — | 1 |
| `InvalidInventoryException` | Violación de una regla del agregado inventario. | 400 `INVALID_INVENTORY` | `Inventory` | 2 |
| `InvalidOperationException` | Un registro `Operation` está incompleto o es inválido. | 500 `INTERNAL_ERROR` (lo construyen los servicios) | `Operation` | 1 |
| `InvalidOrderException` | Violación de una regla del agregado pedido. | 400 `INVALID_ORDER` | `Order`, `OrderItem` | 2 |
| `InvalidProductException` | Violación de una regla del agregado producto. | 400 `INVALID_PRODUCT` | `Product`, `ProductVariant` | 1 |
| `InvalidRefundException` | Violación de una regla del agregado reembolso. | 400 `INVALID_REFUND` | `Refund` | 1 |
| `InvalidReturnException` | Violación de una regla del agregado devolución. | 400 `INVALID_RETURN` | `ProductReturn` | 2 |
| `InvalidShipmentException` | Violación de una regla del agregado envío. | 400 `INVALID_SHIPMENT` | `Shipment` | 1 |
| `InvalidStatusTransitionException` | Un cambio de estado no está permitido por la matriz de transiciones de la entidad. | 409 `INVALID_STATE_TRANSITION` | `Buyer`, `Inventory`, `Order`, `Product`, `ProductReturn`, `Refund` | 0 |
| `InvalidUserException` | Violación de una regla del agregado usuario. | 400 `INVALID_USER` | `Buyer`, `Person`, `Seller`, `User` | 7 |
| `InvalidWarehouseException` | Violación de una regla del agregado bodega. | 400 `INVALID_WAREHOUSE` | `Warehouse` | 4 |
| `UnauthorizedOperationException` | El usuario autenticado no tiene permiso sobre el flujo o recurso solicitado. | 403 `FORBIDDEN` | `User` | 11 |

## Jerarquía

```text
RuntimeException
 └── DomainException
      ├── InvalidCredentialsException
      ├── UnauthorizedOperationException
      ├── EntityNotFoundException
      ├── DuplicateResourceException
      ├── InvalidStatusTransitionException · ImmutableOrderException
      ├── InsufficientStockException · DamagedInventoryException
      ├── InvalidCatalogCodeException
      ├── Invalid{User|Product|Warehouse|Cart|Order|Shipment|Return|Refund|Inventory}Exception
      └── InvalidOperationException · InvalidAuditLogException
```
