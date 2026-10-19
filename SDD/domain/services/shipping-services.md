# Servicios de Envíos

## Introducción

Gestión de los procesos logísticos de los productos físicos (OBJ-10): preparación, despacho y entrega.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **6**.

---

## ConfirmShipmentDeliveryService

Un OPERADOR LOGÍSTICO confirma la entrega de un envío. El pedido pasa a ENTREGADO (finalizado).

```java
Shipment execute(User user, Shipment probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindShipmentService`, `NotifyPersonService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ShipmentRepositoryPort`, `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `ORDER_DELIVERY`, `SHIPMENT_DELIVERY` |

---

## ConsultOrderShipmentService

Consulta el envío de un pedido del comprador autenticado.

```java
Shipment execute(User user, Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindBuyerOrderService` |
| Puertos de salida | `ShipmentRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | — |

---

## ConsultShipmentsService

Consulta los envíos, opcionalmente filtrados por estado (OPERADOR LOGÍSTICO, ADMIN y SUPERVISOR).

```java
List<Shipment> execute(User user, Shipment filter)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `ShipmentRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## CreateShipmentService

Abre el envío (EN PREPARACIÓN) de un pedido pagado con ítems físicos.

```java
Shipment execute(User actor, Order order)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RegisterOperationAndAuditService` |
| Puertos de salida | `ShipmentRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `SHIPMENT_CREATION` |

---

## DispatchShipmentService

Un OPERADOR LOGÍSTICO despacha un envío (salida física de la bodega). El pedido pasa a DESPACHADO.

```java
Shipment execute(User user, Shipment probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindShipmentService`, `NotifyPersonService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ShipmentRepositoryPort`, `OrderRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `SHIPMENT_DISPATCH` |

---

## FindShipmentService

Carga un envío por su identificador o falla con EntityNotFoundException.

```java
Shipment execute(Shipment probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `ShipmentRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidShipmentException` |
| Operaciones auditadas | — |

---
