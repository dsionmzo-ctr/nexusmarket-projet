# Servicios de Reportes administrativos

## Introducción

Consolidación de información administrativa de solo lectura (OBJ-12).

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **1**.

---

## ConsultAdministrativeSummaryService

Construye las cifras consolidadas de solo lectura para la consulta administrativa (ADMIN y SUPERVISOR).

```java
AdministrativeSummary execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `UserRepositoryPort`, `OrderRepositoryPort`, `ProductRepositoryPort`, `ShipmentRepositoryPort`, `RefundRepositoryPort`, `InventoryRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---
