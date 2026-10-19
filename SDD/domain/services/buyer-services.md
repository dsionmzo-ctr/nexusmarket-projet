# Servicios de Compradores

## Introducción

Dominio 2 (Gestión de Compradores): registro, perfil, direcciones y estado comercial. Restricción clave: el comprador nunca administra información de otros compradores ni inventarios.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **6**.

---

## AddBuyerAddressService

Agrega una dirección de entrega adicional al perfil del comprador autenticado.

```java
Buyer execute(User user, Address address)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `BuyerRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `BUYER_ADDRESS_ADDITION` |

---

## ChangeBuyerStatusService

Cambia el estado comercial del comprador (HABILITADO, RESTRINGIDO). Solo el ADMIN puede hacerlo.

```java
Buyer execute(User admin, Buyer buyer, BuyerStatus newStatus)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `BuyerRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | `BUYER_STATUS_CHANGE` |

---

## ConsultBuyerProfileService

Consulta el perfil del comprador autenticado (un comprador nunca lee a otros compradores).

```java
Buyer execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## RegisterBuyerService

Autorregistro de un comprador: valida los datos obligatorios (identificación, nombre, correo, dirección principal) y la unicidad de identificación y correo en la plataforma. Es una operación anónima, por eso la traza Operation/Audit se registra al crear la cuenta de usuario.

```java
Buyer execute(Buyer buyer)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `COMPRADOR` |
| Servicios colaboradores | — |
| Puertos de salida | `BuyerRepositoryPort`, `UserRepositoryPort` |
| Excepciones de dominio | `DuplicateResourceException`, `InvalidUserException` |
| Operaciones auditadas | — |

---

## RemoveBuyerAddressService

Elimina una dirección adicional del perfil del comprador autenticado (la dirección principal no se puede eliminar).

```java
Buyer execute(User user, Address address)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `BuyerRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `BUYER_ADDRESS_REMOVAL` |

---

## ResolveAuthenticatedBuyerService

Resuelve el perfil Buyer del usuario autenticado (rol BUYER, usuario activo) desde el repositorio.

```java
Buyer execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `COMPRADOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `BuyerRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---
