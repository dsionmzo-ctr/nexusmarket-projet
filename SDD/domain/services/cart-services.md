# Servicios de Carrito

## Introducción

Gestión del carrito de compras del comprador (OBJ-07).

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **6**.

---

## AddItemToCartService

Agrega una variante de un producto PUBLICADO al carrito del comprador autenticado; si la variante ya está en el carrito, las cantidades se suman.

```java
Cart execute(User user, CartItem item)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrCreateCartService` |
| Puertos de salida | `ProductRepositoryPort`, `CartRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidCartException` |
| Operaciones auditadas | — |

---

## ClearCartService

Vacía el carrito del comprador autenticado.

```java
Cart execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrCreateCartService` |
| Puertos de salida | `CartRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultCartService

Consulta el carrito del comprador autenticado.

```java
Cart execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrCreateCartService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## FindOrCreateCartService

Busca el carrito de un comprador y lo crea vacío la primera vez.

```java
Cart execute(Buyer buyer)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `CartRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## RemoveCartItemService

Quita una variante del carrito del comprador autenticado.

```java
Cart execute(User user, CartItem item)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrCreateCartService` |
| Puertos de salida | `CartRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## UpdateCartItemQuantityService

Cambia la cantidad de una variante que ya está en el carrito del comprador autenticado.

```java
Cart execute(User user, CartItem item)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedBuyerService`, `FindOrCreateCartService` |
| Puertos de salida | `CartRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---
