# Capa: adaptadores REST

**Paquete:** `application.adapters.rest` — adaptador de **entrada HTTP**.

## Contenido

| Subpaquete | Cantidad | Contenido |
|---|---|---|
| `controllers` | 7 | `AuthRestController`, `PublicCatalogRestController`, `BuyerRestController`, `SellerRestController`, `LogisticOperatorRestController`, `AdminRestController`, `SupervisorRestController` |
| `dtos.requests` | 24 | Records con Jakarta Validation |
| `dtos.responses` | 25 | Records de salida (incluye `ErrorResponseDTO`) |
| `mappers` | 17 | Conversión estática DTO ↔ modelo |
| `exception` | 2 | `ErrorCodeMapping`, `GlobalExceptionHandler` |

## Responsabilidades

1. Traducir HTTP/JSON a llamadas sobre los **puertos de entrada** y las respuestas a JSON.
2. Validar el **formato** (`@Valid`, `@Positive`); el dominio valida las reglas.
3. Obtener el usuario con `@AuthenticationPrincipal AuthenticatedUserPrincipal` y pasar `principal.domainUser()`.
4. Producir el sobre de error estándar y mapear excepciones de dominio a HTTP.

## Lo que **no** hace

- No contiene reglas de negocio, ni accede a repositorios, ni invoca servicios de dominio directamente.
- No expone entidades JPA ni documentos Mongo.
- No devuelve datos sensibles (`passwordHash`, `authTokenVersion`).

## Dependencias

`rest → domain.ports.in` (puertos), `rest → domain.models/valueobjects` (tipos), `rest → adapters.security` (solo `AuthenticatedUserPrincipal` y `RequestIdFilter`).

Documentos de detalle: `Adapters/Rest-adapters.md`, `Adapters/Api-rest-endpoints.md`, `Adapters/Rest-validation.md`, `Adapters/Global-exception-handler.md`.
