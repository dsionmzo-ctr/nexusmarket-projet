# Servicios de Autorización

## Introducción

Responde a «¿qué puede hacer este usuario?». La autenticación (quién es) vive en `user`; aquí solo se decide si una operación puede iniciarla el `User` actual según su `SystemRole`, su estado y la propiedad del recurso (RG-01, RG-02, RG-03 y matriz de responsabilidades). Ningún servicio de esta área ejecuta la operación de negocio.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **9**.

---

## AuthorizeInventoryOperationService

Decide quién puede administrar un registro de inventario: un OPERADOR LOGÍSTICO en cualquier bodega, o un VENDEDOR únicamente sobre sus propios productos almacenados en sus propias bodegas.

```java
void execute(User user, Inventory inventory)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `OPERADOR_LOGÍSTICO`, `VENDEDOR` |
| Servicios colaboradores | `AuthorizeOperationService`, `ValidateSellerOwnershipService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## AuthorizeOperationService

Compuerta común de los servicios de negocio: el usuario debe estar ACTIVO y tener alguno de los roles permitidos (RG-01, RG-03). Compone los dos validadores siguientes.

```java
void execute(User user, SystemRole... allowedRoles)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ValidateUserAuthorizationStatusService`, `ValidateRoleAuthorizationService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## AuthorizeOrderAccessService

Decide el acceso a un pedido: el comprador dueño, el vendedor solo a sus propias líneas (devuelve una vista restringida) y los roles operativos (OPERADOR LOGÍSTICO, ADMIN, SUPERVISOR) a todo el pedido.

```java
Order execute(User user, Order order)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `COMPRADOR`, `OPERADOR_LOGÍSTICO`, `VENDEDOR`, `SUPERVISOR` |
| Servicios colaboradores | `ValidateUserAuthorizationStatusService`, `ValidateBuyerOwnershipService` |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## AuthorizeProductOperationService

Un producto solo puede ser gestionado por el vendedor que lo posee.

```java
void execute(User user, Product product)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `VENDEDOR` |
| Servicios colaboradores | `AuthorizeOperationService`, `ValidateSellerOwnershipService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## AuthorizeWarehouseAccessService

Las bodegas son visibles para ADMIN, OPERADOR LOGÍSTICO y SUPERVISOR; un vendedor solo ve las suyas.

```java
void execute(User user, Warehouse warehouse)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `OPERADOR_LOGÍSTICO`, `VENDEDOR`, `SUPERVISOR` |
| Servicios colaboradores | `ValidateUserAuthorizationStatusService`, `ValidateSellerOwnershipService` |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## ValidateBuyerOwnershipService

Valida que el usuario autenticado sea el comprador dueño del recurso (el comprador nunca administra información de otros compradores).

```java
void execute(User user, Buyer owner)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## ValidateRoleAuthorizationService

Valida que el rol del usuario sea uno de los permitidos para la operación (RG-02 y RG-03: ningún participante administra información fuera de su rol).

```java
void execute(User user, SystemRole... allowedRoles)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## ValidateSellerOwnershipService

Valida que el usuario autenticado sea el vendedor dueño del recurso.

```java
void execute(User user, Seller owner)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## ValidateUserAuthorizationStatusService

Valida que el usuario esté operativamente ACTIVO antes de ejecutar operaciones protegidas (RG-01: toda operación la ejecuta un usuario autenticado).

```java
void execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | — |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---
