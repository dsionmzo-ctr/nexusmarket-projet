# Servicios de Facturación

## Introducción

Gestión de facturación de las compras (OBJ-09).

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **2**.

---

## ConsultOrderInvoiceService

Consulta la factura de un pedido del comprador autenticado.

```java
Invoice execute(User user, Order probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindBuyerOrderService` |
| Puertos de salida | `InvoiceRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | — |

---

## GenerateInvoiceService

Emite la factura de un pedido pagado (subtotal + impuesto de ventas) con numeración consecutiva.

```java
Invoice execute(User actor, Order order)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RegisterOperationAndAuditService` |
| Puertos de salida | `InvoiceRepositoryPort`, `BusinessConfigurationPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `INVOICE_ISSUANCE` |

---
