# Servicios de Devoluciones y reembolsos

## Introducción

Administración de devoluciones y reembolsos (OBJ-11).

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **12**.

---

## ApproveRefundService

El ADMIN aprueba un reembolso. La mercancía devuelta debe haberse recibido antes. La decisión registra quién decidió y cuándo.

```java
Refund execute(User admin, Refund requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindRefundService`, `FindProductReturnService`, `NotifyPersonService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `RefundRepositoryPort` |
| Excepciones de dominio | `DomainException` |
| Operaciones auditadas | `REFUND_APPROVAL` |

---

## ConsultBuyerRefundsService

Consulta los reembolsos del comprador autenticado.

```java
List<Refund> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService` |
| Puertos de salida | `RefundRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultBuyerReturnsService

Consulta las devoluciones del comprador autenticado.

```java
List<ProductReturn> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService` |
| Puertos de salida | `ProductReturnRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultRefundsService

Consulta los reembolsos, opcionalmente filtrados por estado (ADMIN y SUPERVISOR).

```java
List<Refund> execute(User user, Refund filter)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `RefundRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultReturnsToReceiveService

Un OPERADOR LOGÍSTICO consulta las devoluciones pendientes de recibir (estado SOLICITADA).

```java
List<ProductReturn> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `ProductReturnRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## FindProductReturnService

Carga una devolución por su identificador o falla con EntityNotFoundException.

```java
ProductReturn execute(ProductReturn probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `ProductReturnRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidReturnException` |
| Operaciones auditadas | — |

---

## FindRefundService

Carga un reembolso por su identificador o falla con EntityNotFoundException.

```java
Refund execute(Refund probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `RefundRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidRefundException` |
| Operaciones auditadas | — |

---

## ReceiveReturnService

Un OPERADOR LOGÍSTICO recibe en una bodega la mercancía devuelta. Las unidades reingresan al inventario con un movimiento de DEVOLUCIÓN.

```java
ProductReturn execute(User user, ProductReturn requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindProductReturnService`, `FindWarehouseService`, `RestockReturnedGoodsService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductReturnRepositoryPort` |
| Excepciones de dominio | `InvalidWarehouseException` |
| Operaciones auditadas | `RETURN_RECEIPT` |

---

## RejectRefundService

El ADMIN rechaza un reembolso. La decisión registra quién decidió y cuándo.

```java
Refund execute(User admin, Refund requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindRefundService`, `FindProductReturnService`, `NotifyPersonService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `RefundRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `REFUND_REJECTION` |

---

## RejectReturnService

Un OPERADOR LOGÍSTICO rechaza una devolución (mercancía no aceptada). El reembolso abierto con ella también se rechaza.

```java
ProductReturn execute(User user, ProductReturn requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindProductReturnService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductReturnRepositoryPort`, `RefundRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `REFUND_REJECTION`, `RETURN_REJECTION` |

---

## RequestReturnService

El comprador autenticado solicita la devolución de un producto físico entregado y, con ella, el reembolso de su precio. El pedido entregado nunca se modifica (un pedido finalizado es inmutable).

```java
ReturnRequestResult execute(User user, ProductReturn requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindBuyerOrderService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductReturnRepositoryPort`, `RefundRepositoryPort`, `BusinessConfigurationPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidReturnException` |
| Operaciones auditadas | `RETURN_REQUEST` |

---

## RestockReturnedGoodsService

Hace que las unidades devueltas reingresen al inventario de la bodega receptora con un movimiento de DEVOLUCIÓN.

```java
Inventory execute(User actor, ProductReturn productReturn)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindOrCreateInventoryService`, `RecordInventoryMovementService` |
| Puertos de salida | `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---
