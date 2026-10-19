# Servicios de Bodegas

## Introducción

Dominio 4 (Gestión de Bodegas): bodegas del marketplace y de vendedores.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **6**.

---

## ChangeWarehouseStatusService

Activa o desactiva una bodega. Solo el ADMIN puede hacerlo.

```java
Warehouse execute(User admin, Warehouse warehouse, WarehouseStatus newStatus)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindWarehouseService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `WarehouseRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `WAREHOUSE_STATUS_CHANGE` |

---

## ConsultSellerWarehousesService

Consulta las bodegas del vendedor autenticado.

```java
List<Warehouse> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedSellerService` |
| Puertos de salida | `WarehouseRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultWarehousesService

Consulta todas las bodegas (ADMIN, OPERADOR LOGÍSTICO y SUPERVISOR).

```java
List<Warehouse> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `WarehouseRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## CreateWarehouseService

Crea una bodega de tipo MARKETPLACE o VENDEDOR. Solo el ADMIN puede hacerlo.

```java
Warehouse execute(User admin, Warehouse warehouse)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `WarehouseRepositoryPort`, `SellerRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | `WAREHOUSE_CREATION` |

---

## FindWarehouseService

Carga una bodega por su identificador o falla con EntityNotFoundException.

```java
Warehouse execute(Warehouse probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `WarehouseRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidWarehouseException` |
| Operaciones auditadas | — |

---

## UpdateWarehouseService

Actualiza el nombre y la dirección de una bodega. Solo el ADMIN puede hacerlo.

```java
Warehouse execute(User admin, Warehouse changes)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `FindWarehouseService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `WarehouseRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `WAREHOUSE_UPDATE` |

---
