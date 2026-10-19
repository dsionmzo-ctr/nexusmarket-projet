# Servicios de Pedidos

## Introducción

Dominio 7 (Gestión de Pedidos): ciclo de vida del pedido (Carrito → Pendiente de pago → Pagado → Despachado → Entregado/Finalizado). Un pedido finalizado no se modifica bajo ninguna circunstancia.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **10**.

---

## CancelOrderService

Cancela un pedido sin pagar del comprador autenticado y libera su stock reservado.

```java
Order execute(User user, Order requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindBuyerOrderService`, `ReleaseStockService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `ORDER_CANCELLATION` |

---

## CheckoutService

Confirma el carrito del comprador autenticado: valida cada línea contra el catálogo, reserva el stock de los productos físicos, crea el pedido en PENDIENTE DE PAGO y vacía el carrito.

```java
Order execute(User user, Order requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `ReserveStockService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `CartRepositoryPort`, `ProductRepositoryPort`, `OrderRepositoryPort` |
| Excepciones de dominio | `DomainException`, `InvalidOrderException` |
| Operaciones auditadas | `ORDER_CREATION` |

---

## ConsultAllOrdersService

Consulta todos los pedidos del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).

```java
List<Order> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultBuyerOrdersService

Consulta los pedidos del comprador autenticado.

```java
List<Order> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService` |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultOrderService

Consulta un pedido del marketplace (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).

```java
Order execute(User user, Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindOrderService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultSellerOrderService

Consulta un pedido que contiene productos del vendedor autenticado, restringido a las líneas del vendedor.

```java
Order execute(User user, Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `VENDEDOR` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindOrderService`, `AuthorizeOrderAccessService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultSellerOrdersService

Consulta los pedidos que contienen productos del vendedor autenticado, restringidos a las líneas del vendedor.

```java
List<Order> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedSellerService` |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## FindBuyerOrderService

Carga un pedido y verifica que pertenezca al comprador autenticado.

```java
Order execute(User user, Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrderService`, `ValidateBuyerOwnershipService` |
| Puertos de salida | — |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | — |

---

## FindOrderService

Carga un pedido por su identificador o falla con EntityNotFoundException.

```java
Order execute(Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidOrderException` |
| Operaciones auditadas | — |

---

## PayOrderService

Registra el pago de un pedido del comprador autenticado e inicia el flujo posterior: el stock reservado pasa a SALIDA POR VENTA, se emite la factura y, para productos físicos, se abre el envío. Los pedidos compuestos solo por productos digitales se entregan de inmediato.

```java
Order execute(User user, Order requested)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindBuyerOrderService`, `ConfirmStockSaleService`, `GenerateInvoiceService`, `CreateShipmentService`, `NotifyPersonService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `ORDER_DELIVERY`, `ORDER_PAYMENT` |

---
