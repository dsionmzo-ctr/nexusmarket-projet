# Servicios de Vendedores

## Introducción

Dominio 3 (Gestión de Vendedores): los vendedores no se autorregistran; los incorpora el ADMIN junto con su primera bodega.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **3**.

---

## IncorporateSellerService

Coordina el flujo de incorporación del negocio: el ADMIN registra al vendedor y su primera bodega (la bodega se crea para el nuevo vendedor). Se ejecuta en una sola transacción.

```java
Seller execute(User admin, Seller seller, Warehouse firstWarehouse)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `RegisterSellerService`, `CreateWarehouseService` |
| Puertos de salida | — |
| Excepciones de dominio | `InvalidWarehouseException` |
| Operaciones auditadas | — |

---

## RegisterSellerService

Incorpora a un vendedor. Los vendedores no pueden autorregistrarse: solo un ADMIN puede hacerlo. La identificación y el correo deben ser únicos en la plataforma.

```java
Seller execute(User admin, Seller seller)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `SellerRepositoryPort`, `UserRepositoryPort` |
| Excepciones de dominio | `DuplicateResourceException`, `InvalidUserException` |
| Operaciones auditadas | `SELLER_REGISTRATION` |

---

## ResolveAuthenticatedSellerService

Resuelve el perfil Seller del usuario autenticado (rol SELLER, usuario activo) desde el repositorio.

```java
Seller execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `VENDEDOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `SellerRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---
