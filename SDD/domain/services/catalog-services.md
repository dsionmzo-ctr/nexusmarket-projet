# Servicios de Catálogo

## Introducción

Dominio 5 (Gestión del Catálogo): productos físicos y digitales, variantes y estados PUBLICADO / SUSPENDIDO / DESCONTINUADO, más el catálogo público.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **12**.

---

## AddProductVariantService

Un VENDEDOR agrega una variante (color, talla, modelo…) a uno de sus productos. El SKU debe ser único.

```java
Product execute(User user, Product probe, ProductVariant variant)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindSellerProductService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | `DuplicateResourceException` |
| Operaciones auditadas | `PRODUCT_VARIANT_ADDITION` |

---

## ConsultAllProductsService

Consulta todos los productos del catálogo en cualquier estado (ADMIN y SUPERVISOR).

```java
List<Product> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultPublishedProductService

Catálogo público: consulta un producto PUBLICADO. Un producto no publicado se informa como no encontrado.

```java
Product execute(Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindProductService` |
| Puertos de salida | — |
| Excepciones de dominio | `EntityNotFoundException` |
| Operaciones auditadas | — |

---

## ConsultPublishedProductsService

Catálogo público: consulta los productos PUBLICADOS (sin autenticación).

```java
List<Product> execute()
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## ConsultSellerProductsService

Consulta los productos del vendedor autenticado en cualquier estado.

```java
List<Product> execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedSellerService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## DiscontinueProductService

Un VENDEDOR descontinúa de forma permanente uno de sus productos.

```java
Product execute(User user, Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindSellerProductService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `PRODUCT_DISCONTINUATION` |

---

## FindProductService

Carga un producto por su identificador o falla con EntityNotFoundException.

```java
Product execute(Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidProductException` |
| Operaciones auditadas | — |

---

## FindSellerProductService

Carga un producto y verifica que el usuario autenticado sea el vendedor dueño.

```java
Product execute(User user, Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindProductService`, `AuthorizeProductOperationService` |
| Puertos de salida | — |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## PublishProductService

Un VENDEDOR publica (SUSPENDIDO → PUBLICADO) uno de sus productos.

```java
Product execute(User user, Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindSellerProductService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `PRODUCT_PUBLICATION` |

---

## RegisterProductService

Un VENDEDOR registra uno de sus productos con sus variantes. El producto se crea SUSPENDIDO (no visible) y el SKU de cada variante debe ser único en el catálogo.

```java
Product execute(User user, Product product)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `ResolveAuthenticatedSellerService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | `DuplicateResourceException` |
| Operaciones auditadas | `PRODUCT_REGISTRATION` |

---

## SuspendProductService

Un VENDEDOR suspende (PUBLICADO → SUSPENDIDO) uno de sus productos.

```java
Product execute(User user, Product probe)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindSellerProductService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `PRODUCT_SUSPENSION` |

---

## UpdateProductService

Un VENDEDOR actualiza el nombre, la descripción y el precio de uno de sus productos.

```java
Product execute(User user, Product changes)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | `FindSellerProductService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `ProductRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | `PRODUCT_UPDATE` |

---
