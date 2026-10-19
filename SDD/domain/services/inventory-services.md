# Servicios de Inventario

## Introducción

Dominio 6 (Gestión del Inventario): inventario distribuido por producto y bodega, libro de movimientos (Ingreso, Reserva, Salida por venta, Ajuste, Devolución) y prohibición de existencias negativas.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **13**.

---

## AdjustInventoryService

Un OPERADOR LOGÍSTICO corrige el stock físico de un registro de inventario (AJUSTE). El stock nunca puede quedar negativo ni inferior a lo reservado.

```java
Inventory execute(User user, InventoryMovement movement)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindInventoryService`, `RecordInventoryMovementService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `INVENTORY_ADJUSTMENT` |

---

## ConfirmStockSaleService

Convierte en SALIDA POR VENTA el stock reservado por un pedido recién pagado (las unidades salen de la bodega).

```java
void execute(User actor, Order order)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RecordInventoryMovementService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | `STOCK_SALE` |

---

## ConsultInventoryMovementsService

Consulta el libro de movimientos de un registro de inventario (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).

```java
List<InventoryMovement> execute(User user, Inventory inventory)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindInventoryService` |
| Puertos de salida | `InventoryMovementRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultSellerInventoryService

Consulta el inventario de los productos del vendedor autenticado.

```java
List<Inventory> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedSellerService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultWarehouseInventoryService

Consulta el inventario almacenado en una bodega (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR; un VENDEDOR solo en sus propias bodegas).

```java
List<Inventory> execute(User user, Warehouse probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindWarehouseService`, `AuthorizeWarehouseAccessService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## FindInventoryService

Carga el inventario de una variante en una bodega (ambas tomadas del modelo recibido) o falla con EntityNotFoundException.

```java
Inventory execute(Inventory probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidInventoryException` |
| Operaciones auditadas | — |

---

## FindOrCreateInventoryService

Busca el registro de inventario de una variante en una bodega y lo crea (vacío) en el primer ingreso.

```java
Inventory execute(Product product, ProductVariant variant, Warehouse warehouse)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## MarkInventoryAvailableService

Un OPERADOR LOGÍSTICO vuelve a marcar como DISPONIBLE un registro de inventario dañado.

```java
Inventory execute(User user, InventoryMovement movement)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindInventoryService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `INVENTORY_AVAILABILITY_RESTORATION` |

---

## MarkInventoryDamagedService

Un OPERADOR LOGÍSTICO marca un registro de inventario como DAÑADO: deja de poder reservarse.

```java
Inventory execute(User user, InventoryMovement movement)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindInventoryService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `INVENTORY_DAMAGE_MARKING` |

---

## RecordInventoryMovementService

Agrega una entrada al libro inmutable de movimientos de inventario (Ingreso, Reserva, Salida por venta, Ajuste, Devolución).

```java
InventoryMovement execute(User actor, Inventory inventory, MovementType type, int quantity, String note)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `InventoryMovementRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## RegisterInventoryInflowService

Registra stock que entra a una bodega (INGRESO) para la variante de un producto físico. Un VENDEDOR solo puede hacerlo sobre sus productos en sus bodegas; un OPERADOR LOGÍSTICO en cualquier bodega. El registro de inventario se crea en el primer ingreso.

```java
Inventory execute(User user, InventoryMovement movement)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `AuthorizeInventoryOperationService`, `FindWarehouseService`, `FindOrCreateInventoryService`, `RecordInventoryMovementService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort`, `InventoryRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidInventoryException`, `InvalidWarehouseException` |
| Operaciones auditadas | `INVENTORY_INFLOW` |

---

## ReleaseStockService

Devuelve el stock reservado por un pedido que no se pagará (pedido cancelado).

```java
void execute(User actor, Order order)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RecordInventoryMovementService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | `STOCK_RELEASE` |

---

## ReserveStockService

Reserva las unidades de una variante tomándolas de las bodegas que pueden atenderlas (bodega activa, stock DISPONIBLE y no dañado, mayor disponibilidad primero). No se reserva nada si el stock total disponible no alcanza: nunca se reserva inventario inexistente o dañado.

```java
List<StockAllocation> execute(User actor, ProductVariant variant, int quantity)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RecordInventoryMovementService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | `InsufficientStockException` |
| Operaciones auditadas | `STOCK_RESERVATION` |

---
