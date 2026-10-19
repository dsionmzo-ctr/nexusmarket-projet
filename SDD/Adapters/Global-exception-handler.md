# Manejador global de excepciones

## Introducción

`GlobalExceptionHandler` (`@RestControllerAdvice`) convierte **todo fallo** en una respuesta JSON con el mismo sobre, un código de máquina estable y el `requestId` de la petición. El cliente nunca ve trazas de pila, nombres de clases internas ni detalles de SQL.

La correspondencia excepción de dominio → HTTP está aislada en `ErrorCodeMapping` (clase sin tipos de Spring, probada de forma unitaria en `ErrorCodeMappingTest`). El manejador solo adapta los tipos de Spring.

## Sobre de error

```json
{
  "timestamp": "2026-10-18T15:04:05Z",
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Not enough stock for variant 12.",
  "path": "/api/v1/buyer/orders",
  "requestId": "b3c1e7a2-6b0d-4f36-9a8e-0d9d7a5e1f10",
  "details": null
}
```

| Campo | Significado |
|---|---|
| `timestamp` | Instante del error (se obtiene del `Clock` inyectado) |
| `status` | Código HTTP |
| `code` | Código estable de máquina |
| `message` | Mensaje seguro para el usuario |
| `path` | Ruta solicitada |
| `requestId` | Valor de `X-Request-Id` (aceptado o generado) |
| `details` | Detalle por campo en errores de validación; `null` en el resto |

## Catálogo de códigos

| HTTP | `code` | Cuándo |
|---|---|---|
| 400 | `INVALID_REQUEST` | JSON mal formado, tipo erróneo, propiedad desconocida, parámetro faltante, validación Jakarta, código de catálogo inexistente |
| 400 | `INVALID_USER` / `INVALID_PRODUCT` / `INVALID_WAREHOUSE` / `INVALID_CART` / `INVALID_ORDER` / `INVALID_SHIPMENT` / `INVALID_RETURN` / `INVALID_REFUND` / `INVALID_INVENTORY` | Regla de negocio incumplida sobre esa entidad |
| 401 | `AUTHENTICATION_REQUIRED` | Falta el token o es inválido/vencido/obsoleto |
| 401 | `INVALID_CREDENTIALS` | Correo o contraseña incorrectos (misma respuesta para ambos) |
| 403 | `FORBIDDEN` | Rol insuficiente o recurso ajeno (`UnauthorizedOperationException`, `AccessDeniedException`) |
| 404 | `RESOURCE_NOT_FOUND` | Entidad inexistente o ruta inexistente |
| 405 | `METHOD_NOT_ALLOWED` | Método HTTP no soportado |
| 406 | `NOT_ACCEPTABLE` | `Accept` no satisfacible |
| 409 | `RESOURCE_ALREADY_EXISTS` | Identificación, correo, SKU u otro dato único repetido |
| 409 | `INVALID_STATE_TRANSITION` | Transición de estado inválida o pedido finalizado (`ImmutableOrderException`) |
| 409 | `INSUFFICIENT_STOCK` | Stock disponible insuficiente |
| 409 | `DAMAGED_INVENTORY` | Intento de reservar inventario dañado |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | `Content-Type` distinto de JSON |
| 500 | `INTERNAL_ERROR` | Error no previsto (el detalle solo se registra en el log) |
| 503 | `DEPENDENCY_UNAVAILABLE` | PostgreSQL o MongoDB no disponible |

## Excepciones manejadas

| Excepción | Resultado |
|---|---|
| `DomainException` y subclases | `ErrorCodeMapping.forDomainException` |
| `MethodArgumentNotValidException` | 400 con `details` por campo |
| `HandlerMethodValidationException`, `ConstraintViolationException` | 400 con `details` (parámetros de ruta/consulta) |
| `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, `MissingServletRequestParameterException` | 400 |
| `AccessDeniedException` | 403 |
| `DataIntegrityViolationException` | **409 solo si** el `SQLState` es `23505` (violación de unicidad) o es `DuplicateKeyException`; en otro caso es un error interno 500 |
| `DataAccessResourceFailureException`, `CannotCreateTransactionException`, `TransientDataAccessException` | 503 |
| Errores de Spring MVC con estado propio (`ErrorResponse`: 404, 405, 406, 415…) | `ErrorCodeMapping.forHttpStatus` |
| Cualquier otra | 500 `INTERNAL_ERROR` |

## Reglas

- Los mensajes de `DomainException` se consideran seguros y se devuelven tal cual; los errores 500 y 503 devuelven un mensaje genérico.
- Las excepciones de trazabilidad (`InvalidOperationException`, `InvalidAuditLogException`) se asignan a 500 porque las construyen los servicios, no el cliente.
- Toda respuesta de error incluye `X-Request-Id` en cabecera y cuerpo para correlacionarla con el log.
- Los errores de autenticación/autorización producidos **antes** de los controladores se escriben con `SecurityErrorResponseWriter`, con el mismo formato.
