# Adaptadores REST

## Introducción

Los **adaptadores REST** (`application.adapters.rest`) son el adaptador *primario* (de entrada) de la arquitectura hexagonal: traducen HTTP/JSON a llamadas sobre los **puertos de entrada** del dominio y las respuestas del dominio a JSON. No contienen reglas de negocio.

```text
Cliente HTTP ──JSON──▶ Controller ──DTO──▶ Mapper ──modelo──▶ Puerto de entrada
                                                                     │
Cliente HTTP ◀──JSON── Controller ◀──DTO── Mapper ◀──modelo─────────┘
```

## Estructura

```text
adapters/rest
├── controllers   # 7 controladores (uno por rol + autenticación + catálogo público)
├── dtos
│   ├── requests  # 24 DTO de petición (records con Jakarta Validation)
│   └── responses # 25 DTO de respuesta (records, incluye ErrorResponseDTO)
├── mappers       # 17 mappers estáticos DTO ↔ modelo de dominio
└── exception     # ErrorCodeMapping y GlobalExceptionHandler
```

## Controladores

| Controlador | Prefijo | Puerto de entrada | Rol |
|---|---|---|---|
| `AuthRestController` | `/api/v1/auth` | `PublicAccessPort` | Público (logout: autenticado) |
| `PublicCatalogRestController` | `/api/v1/catalog` | `PublicAccessPort` | Público |
| `BuyerRestController` | `/api/v1/buyer` | `BuyerPort` | `BUYER` |
| `SellerRestController` | `/api/v1/seller` | `SellerPort` | `SELLER` |
| `LogisticOperatorRestController` | `/api/v1/logistic-operator` | `LogisticOperatorPort` | `LOGISTIC_OPERATOR` |
| `AdminRestController` | `/api/v1/admin` | `AdminPort` | `ADMIN` |
| `SupervisorRestController` | `/api/v1/supervisor` | `SupervisorPort` | `SUPERVISOR` |

Reglas para todo controlador:

1. Depende **solo** del puerto de entrada de su rol (nunca de servicios de dominio ni de repositorios).
2. Recibe el usuario con `@AuthenticationPrincipal AuthenticatedUserPrincipal principal` y pasa `principal.domainUser()` al puerto: **la identidad nunca viene del cuerpo ni de la ruta** (un comprador no puede «hacerse pasar» por otro enviando su id).
3. Valida con `@Valid` el cuerpo y con `@Positive` los ids de ruta.
4. Cada método es de una línea: `Mapper.toResponse(puerto.caso(principal.domainUser(), Mapper.toDomain(request)))`.
5. Usa `@ResponseStatus(HttpStatus.CREATED)` en altas y `204` en logout.
6. No captura excepciones: las traduce `GlobalExceptionHandler`.

El detalle de los **81 endpoints** está en `Api-rest-endpoints.md`.

## DTO

- **Entrada (`requests`)**: `record` inmutables con restricciones Jakarta (`@NotBlank`, `@Size`, `@Email`, `@Positive`, `@Digits`, `@Valid` en anidados). Ver `Rest-validation.md`.
- **Salida (`responses`)**: `record` que exponen solo lo necesario. Nunca contienen `passwordHash`, `rawPassword` ni `authTokenVersion`.
- **Catálogos**: viajan como `String` con su `code` (`"PAID"`, `"PHYSICAL"`).
- **Dinero**: `MoneyResponseDTO(amount, currency)`; en la entrada, `priceAmount` + `priceCurrency`.
- **Fechas**: ISO-8601.
- Jackson está configurado con `fail-on-unknown-properties=true` y `fail-on-null-for-primitives=true`.

## Mappers

Clases utilitarias estáticas (`XxxRestMapper`) con tres tipos de método:

| Método | Uso |
|---|---|
| `toDomain(request)` / `toChanges(id, request)` | Construye un modelo de dominio a partir de un DTO de petición |
| `toProbe(id)` | Construye un modelo «sonda» con solo el id, para consultar o actuar sobre un recurso por identificador |
| `toResponse(model)` | Convierte un modelo en su DTO de respuesta |

Mappers (17): `AddressRestMapper`, `AdministrativeSummaryRestMapper`, `AuditLogRestMapper`, `BuyerRestMapper`, `CartRestMapper`, `InventoryRestMapper`, `InvoiceRestMapper`, `MoneyRestMapper`, `OperationRestMapper`, `OrderRestMapper`, `ProductRestMapper`, `RefundRestMapper`, `ReturnRestMapper`, `SellerRestMapper`, `ShipmentRestMapper`, `UserRestMapper`, `WarehouseRestMapper`.

Los mappers REST convierten códigos con `Catalog.fromCode(...)`; un código inexistente lanza `InvalidCatalogCodeException` (→ 400 `INVALID_REQUEST`).

## Decisiones de diseño relevantes

- **`DELETE /api/v1/buyer/addresses` lleva cuerpo** (`AddressRequestDTO`): una dirección se identifica por sus tres partes, no por un id.
- **Acciones de estado como `PATCH`/`POST` sobre sub-recursos** (`/payment`, `/cancel`, `/dispatch`, `/delivery`, `/reception`, `/rejection`, `/approval`): cada acción de negocio es un caso de uso con su propia autorización y traza, no una edición genérica del estado.
- **Auditoría paginada:** `GET …/audit-logs?page&size&userId&operationType` (page ≥ 0, size 1..100, por defecto 20).
- **Vistas restringidas:** el vendedor recibe de un pedido solo sus propias líneas (`AuthorizeOrderAccessService` / `Order.forSeller`).
