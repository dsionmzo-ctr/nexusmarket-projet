# Validación de peticiones REST

## Introducción

La validación se reparte en capas y **cada capa responde solo por lo suyo**. Esto permite detectar el error lo antes posible y con el código de error correcto.

## Orden de validación

| # | Capa | Qué valida | Error |
|---|---|---|---|
| 1 | Transporte | Método HTTP, `Content-Type`, `Accept` | 405, 415, 406 |
| 2 | Forma | JSON bien formado, tipos correctos, sin propiedades desconocidas (`fail-on-unknown-properties`), sin `null` en primitivos | 400 `INVALID_REQUEST` |
| 3 | Formato | Jakarta Validation sobre cada DTO (`@NotBlank`, `@Size`, `@Email`, `@Positive`, `@Digits`, `@Pattern`…) y `@Positive` sobre ids de ruta | 400 `INVALID_REQUEST` con `details` por campo |
| 4 | Entre campos | Combinaciones de campos del mismo DTO | 400 `INVALID_REQUEST` |
| 5 | Dominio | Invariantes y reglas de negocio de modelos y servicios (unicidad, estados, stock, propiedad) | 400 `INVALID_<ENTIDAD>`, 404, 409 |

## Paginación

`GET /api/v1/admin/audit-logs` y `GET /api/v1/supervisor/audit-logs` aceptan `page` (≥ 0, por defecto 0) y `size` (1..100, por defecto 20). Valores fuera de rango devuelven 400.

## Principios

- Los DTO de petición **no son modelos de dominio**: se transforman con mappers estáticos y el dominio vuelve a validar sus invariantes.
- Los DTO son `record` inmutables; los catálogos llegan como `String` (`type`, `paymentMethod`, `reason`…) y el mapper los convierte con `Catalog.fromCode(...)`, que rechaza códigos desconocidos.
- El `Content-Type` debe ser `application/json`.
- Las contraseñas nunca se devuelven ni se escriben en logs ni en auditoría.

## DTO de petición (24)

### AddressRequestDTO

Se usa en: `POST /api/v1/buyer/addresses`, `DELETE /api/v1/buyer/addresses`

| Campo | Tipo | Restricciones |
|---|---|---|
| `street` | `String` | `@NotBlank @Size(max = 200)` |
| `city` | `String` | `@NotBlank @Size(max = 100)` |
| `country` | `String` | `@NotBlank @Size(max = 100)` |

### CartItemRequestDTO

Se usa en: `POST /api/v1/buyer/cart/items`

| Campo | Tipo | Restricciones |
|---|---|---|
| `variantId` | `Long` | `@NotNull @Positive` |
| `quantity` | `Integer` | `@NotNull @Positive` |

### ChangeStatusRequestDTO

Se usa en: `PATCH /api/v1/admin/users/{userId}/status`, `PATCH /api/v1/admin/buyers/{buyerId}/status`, `PATCH /api/v1/admin/warehouses/{warehouseId}/status`

| Campo | Tipo | Restricciones |
|---|---|---|
| `status` | `String` | `@NotBlank @Size(max = 40)` |

### CheckoutRequestDTO

Se usa anidado dentro de otro DTO de petición.

| Campo | Tipo | Restricciones |
|---|---|---|
| `shippingAddress` | `AddressRequestDTO` | `@Valid` |

### CreateWarehouseRequestDTO

Se usa en: `POST /api/v1/admin/warehouses`

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `type` | `String` | `@NotBlank @Size(max = 40)` |
| `address` | `AddressRequestDTO` | `@NotNull @Valid` |
| `sellerId` | `Long` | `@Positive` |

### FirstWarehouseRequestDTO

Se usa anidado dentro de otro DTO de petición.

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `address` | `AddressRequestDTO` | `@NotNull @Valid` |

### InventoryAdjustmentRequestDTO

Se usa en: `PATCH /api/v1/logistic-operator/inventory/{inventoryId}/adjustment`

| Campo | Tipo | Restricciones |
|---|---|---|
| `newQuantity` | `Integer` | `@NotNull @PositiveOrZero` |
| `note` | `String` | `@NotBlank @Size(max = 500)` |

### InventoryInflowRequestDTO

Se usa en: `POST /api/v1/seller/inventory/inflows`, `POST /api/v1/logistic-operator/inventory/inflows`

| Campo | Tipo | Restricciones |
|---|---|---|
| `variantId` | `Long` | `@NotNull @Positive` |
| `warehouseId` | `Long` | `@NotNull @Positive` |
| `quantity` | `Integer` | `@NotNull @Positive` |
| `note` | `String` | `@Size(max = 500)` |

### InventoryNoteRequestDTO

Se usa anidado dentro de otro DTO de petición.

| Campo | Tipo | Restricciones |
|---|---|---|
| `note` | `String` | `@Size(max = 500)` |

### LoginRequestDTO

Se usa en: `POST /api/v1/auth/login`

| Campo | Tipo | Restricciones |
|---|---|---|
| `email` | `String` | `@NotBlank @Email @Size(max = 160)` |
| `password` | `String` | `@NotBlank @Size(min = 8, max = 100)` |

### PayOrderRequestDTO

Se usa en: `PATCH /api/v1/buyer/orders/{orderId}/payment`

| Campo | Tipo | Restricciones |
|---|---|---|
| `paymentMethod` | `String` | `@NotBlank @Size(max = 40)` |

### ProductVariantRequestDTO

Se usa en: `POST /api/v1/seller/products/{productId}/variants`

| Campo | Tipo | Restricciones |
|---|---|---|
| `sku` | `String` | `@NotBlank @Size(max = 80)` |
| `attributes` | `Map<String, String>` | — |

### ReceiveReturnRequestDTO

Se usa en: `PATCH /api/v1/logistic-operator/returns/{returnId}/reception`

| Campo | Tipo | Restricciones |
|---|---|---|
| `warehouseId` | `Long` | `@NotNull @Positive` |

### RefundDecisionRequestDTO

Se usa anidado dentro de otro DTO de petición.

| Campo | Tipo | Restricciones |
|---|---|---|
| `note` | `String` | `@Size(max = 500)` |

### RegisterBuyerRequestDTO

Se usa en: `POST /api/v1/auth/register/buyer`

| Campo | Tipo | Restricciones |
|---|---|---|
| `identification` | `?$", message = "must not have surrounding whitespace") String` | `@NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)` |
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `email` | `String` | `@NotBlank @Email @Size(max = 160)` |
| `primaryAddress` | `AddressRequestDTO` | `@NotNull @Valid` |

### RegisterProductRequestDTO

Se usa en: `POST /api/v1/seller/products`

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `String` | `@NotBlank @Size(max = 200)` |
| `description` | `String` | `@Size(max = 2000)` |
| `type` | `String` | `@NotBlank @Size(max = 40)` |
| `priceAmount` | `BigDecimal` | `@NotNull @Positive @Digits(integer = 17, fraction = 2)` |
| `priceCurrency` | `String` | `@NotBlank @Size(max = 40)` |
| `variants` | `List<@Valid ProductVariantRequestDTO>` | `@NotEmpty` |

### RegisterSellerRequestDTO

Se usa en: `POST /api/v1/admin/sellers`

| Campo | Tipo | Restricciones |
|---|---|---|
| `identification` | `?$", message = "must not have surrounding whitespace") String` | `@NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)` |
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `email` | `String` | `@NotBlank @Email @Size(max = 160)` |
| `businessName` | `String` | `@NotBlank @Size(max = 160)` |
| `firstWarehouse` | `FirstWarehouseRequestDTO` | `@NotNull @Valid` |

### RegisterSellerUserRequestDTO

Se usa en: `POST /api/v1/admin/seller-users`

| Campo | Tipo | Restricciones |
|---|---|---|
| `sellerIdentification` | `?$", message = "must not have surrounding whitespace") String` | `@NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)` |
| `password` | `String` | `@NotBlank @Size(min = 8, max = 100)` |

### RegisterStaffUserRequestDTO

Se usa en: `POST /api/v1/admin/staff-users`

| Campo | Tipo | Restricciones |
|---|---|---|
| `identification` | `?$", message = "must not have surrounding whitespace") String` | `@NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)` |
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `email` | `String` | `@NotBlank @Email @Size(max = 160)` |
| `password` | `String` | `@NotBlank @Size(min = 8, max = 100)` |
| `role` | `String` | `@NotBlank @Pattern(regexp = "LOGISTIC_OPERATOR|SUPERVISOR|ADMIN", message = "must be LOGISTIC_OPERATOR, SUPERVISOR or ADMIN")` |

### RegisterUserRequestDTO

Se usa en: `POST /api/v1/auth/register/user`

| Campo | Tipo | Restricciones |
|---|---|---|
| `buyerIdentification` | `?$", message = "must not have surrounding whitespace") String` | `@NotBlank @Size(max = 30) @Pattern(regexp = "^\\S(.*\\S)` |
| `password` | `String` | `@NotBlank @Size(min = 8, max = 100)` |

### RequestReturnRequestDTO

Se usa en: `POST /api/v1/buyer/returns`

| Campo | Tipo | Restricciones |
|---|---|---|
| `orderId` | `Long` | `@NotNull @Positive` |
| `variantId` | `Long` | `@NotNull @Positive` |
| `quantity` | `Integer` | `@NotNull @Positive` |
| `reason` | `String` | `@NotBlank @Size(max = 40)` |
| `comment` | `String` | `@Size(max = 500)` |

### UpdateCartItemRequestDTO

Se usa en: `PUT /api/v1/buyer/cart/items/{variantId}`

| Campo | Tipo | Restricciones |
|---|---|---|
| `quantity` | `Integer` | `@NotNull @Positive` |

### UpdateProductRequestDTO

Se usa en: `PUT /api/v1/seller/products/{productId}`

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `String` | `@Size(max = 200)` |
| `description` | `String` | `@Size(max = 2000)` |
| `priceAmount` | `BigDecimal` | `@Positive @Digits(integer = 17, fraction = 2)` |
| `priceCurrency` | `String` | `@Size(max = 40)` |

### UpdateWarehouseRequestDTO

Se usa en: `PUT /api/v1/admin/warehouses/{warehouseId}`

| Campo | Tipo | Restricciones |
|---|---|---|
| `name` | `String` | `@NotBlank @Size(max = 120)` |
| `address` | `AddressRequestDTO` | `@NotNull @Valid` |
