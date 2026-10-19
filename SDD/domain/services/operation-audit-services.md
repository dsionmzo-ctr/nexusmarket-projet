# Servicios de Operaciones y auditoría

## Introducción

Trazabilidad: toda acción relevante genera una `Operation` (PostgreSQL) y un `AuditLog` inmutable (MongoDB).

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **9**.

> `AuditDetails` (en el mismo paquete) no es un servicio: es un constructor utilitario, seguro ante nulos, de los detalles específicos de cada operación; las entradas con valor nulo se omiten.

---

## ConsultAllOperationsService

Consulta todas las operaciones registradas por el sistema (ADMIN y SUPERVISOR).

```java
List<Operation> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `OperationRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultAuditLogsService

Consulta la traza de auditoría (ADMIN y SUPERVISOR). El filtro puede traer el usuario ejecutor y/o el tipo de operación; si vienen ambos, se aplican ambas condiciones.

```java
List<AuditLog> execute(User user, AuditLog filter)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `AuditLogRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultProductAuditTrailService

Consulta los registros de auditoría que afectaron a un recurso específico del marketplace (ADMIN y SUPERVISOR).

```java
List<AuditLog> execute(User user, MarketplaceResource resource)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `AuditLogRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultProductOperationsService

Consulta las operaciones que afectaron a un recurso específico del marketplace (ADMIN y SUPERVISOR).

```java
List<Operation> execute(User user, MarketplaceResource resource)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `OperationRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultUserAuditTrailService

Consulta los registros de auditoría de las operaciones ejecutadas por un usuario específico (ADMIN y SUPERVISOR).

```java
List<AuditLog> execute(User requester, User performer)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `AuditLogRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultUserOperationsService

Consulta las operaciones ejecutadas por el usuario autenticado (cualquier rol: «mis operaciones»).

```java
List<Operation> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ValidateUserAuthorizationStatusService` |
| Puertos de salida | `OperationRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## RegisterAuditLogService

Registra un AuditLog inmutable mediante AuditLogRepositoryPort. Los registros de auditoría son solo-agregar: este servicio no expone actualización.

```java
AuditLog execute(AuditLog auditLog)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `AuditLogRepositoryPort` |
| Excepciones de dominio | `InvalidAuditLogException` |
| Operaciones auditadas | — |

---

## RegisterOperationAndAuditService

Punto único de registro de trazabilidad de los servicios de negocio: persiste la Operation y construye y persiste el AuditLog correspondiente (tipo, fecha, usuario, rol, recurso y detalles).

```java
Operation execute(Operation operation, Map<String, Object> auditDetails)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RegisterOperationService`, `RegisterAuditLogService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## RegisterOperationService

Registra una Operation de negocio mediante OperationRepositoryPort tras validar la información disponible en el modelo Operation (solo trazabilidad; nunca decide la validez del negocio).

```java
Operation execute(Operation operation)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `OperationRepositoryPort` |
| Excepciones de dominio | `InvalidOperationException` |
| Operaciones auditadas | — |

---
