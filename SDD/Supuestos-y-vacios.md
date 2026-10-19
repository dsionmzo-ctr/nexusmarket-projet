# Supuestos, decisiones de diseño y vacíos de la especificación

> La especificación funcional entregada es deliberadamente breve (no incluye tipos de dato, longitudes, DTO, endpoints ni reglas de impuestos o plazos). Todo lo que no estaba escrito se decidió siguiendo el proyecto de referencia y se lista aquí para que sea fácil de revisar y de cambiar.

## 1. Alcance y fuentes

| # | Decisión | Motivo |
|---|---|---|
| 1 | La estructura, el estilo y los documentos del `SDD/` siguen el repositorio de referencia del profesor (banco), adaptados al contexto de NexusMarket | Instrucción del usuario: «es lo mismo que debe tener NexusMarket, dentro del contexto de Nexus» |
| 2 | Documentación en **español**; código Java en **inglés** | Rúbrica (criterio «Idioma y convenciones») |
| 3 | El `SDD/` se **generó a partir del código** (servicios, modelos, puertos, DTO, entidades y controladores) | Garantiza correlación 1:1 SDD ↔ Java |

## 2. Vacíos de la especificación y cómo se resolvieron

| # | Vacío | Decisión |
|---|---|---|
| 1 | **Matriz de responsabilidades** con columnas desalineadas | Se interpretó por semántica (ver `especificacion_funcional_nexusmarket.md` §12). Ajustable cambiando los roles de `AuthorizeOperationService` en cada servicio |
| 2 | **Autenticación**: la especificación la declara fuera de alcance, pero exige RG-01 | JWT stateless con usuario recargado de la base de datos, como el proyecto de referencia |
| 3 | **Impuesto** de la factura | 19 %, configurable (`BUSINESS_TAX_RATE`) |
| 4 | **Ventana de devolución** | 30 días desde la entrega, configurable (`BUSINESS_RETURN_WINDOW_DAYS`) |
| 5 | **Moneda** | Catálogo `Currency` (COP, USD, EUR); no hay conversión entre monedas |
| 6 | **Métodos de pago** | Catálogo `PaymentMethod`; el pago se **simula** (no hay pasarela): registrar el método marca el pedido como pagado |
| 7 | **Estado «Carrito»** del pedido | El carrito es un agregado propio (`Cart`); al confirmarlo nace el `Order` en `PENDING_PAYMENT`. Los estados de la especificación se cubren sin un estado `CART` en `OrderStatus` |
| 8 | **Estado `CANCELLED`** | No está en la especificación; se añadió para cancelar pedidos no pagados liberando la reserva |
| 9 | **Estado comercial del comprador** | Catálogo `BuyerStatus` (`ENABLED`, `RESTRICTED`) |
| 10 | **Estado de usuario** | `ACTIVE`, `INACTIVE`, `BLOCKED` (la especificación dice «Activo, Bloqueado, etc.») |
| 11 | **Longitudes y formatos** de campos | Definidas en los DTO (`Rest-validation.md`) |
| 12 | **Pedidos digitales** | Se entregan al pagar, sin envío ni inventario |
| 13 | **Reserva multi-bodega** | Una línea puede atenderse desde varias bodegas (mayor disponibilidad primero); si el total no alcanza no se reserva nada |
| 14 | **Reembolso** | Se abre junto con la devolución; solo se aprueba tras recibir la mercancía |
| 15 | **Bitácora** | La especificación no la menciona; se incluyó por requisito de «trazabilidad» y por ser parte del modelo de referencia |
| 16 | **Notificaciones** | Puerto de salida con adaptador *no-op* (punto de extensión) |

## 3. Decisiones técnicas y limitaciones conocidas

| # | Tema | Detalle |
|---|---|---|
| 1 | **JWT mínimo** | El token solo lleva `sub`, `jti`, `ver`, `iat`, `exp`, `iss`. El `enunciado evaluativo.md` menciona un JWT «con datos suficientes para reconstruir el `User`»; se siguió el SDD de referencia (`Orchestrator-prompt.md` fase 4: sin PII ni snapshots de permisos) y el usuario se reconstruye desde la base de datos en cada petición |
| 2 | **Consistencia PostgreSQL/MongoDB** | `Operation` y `AuditLog` no comparten transacción. Mejora futura: patrón *outbox* |
| 3 | **`ddl-auto=update`** | Adecuado para el proyecto académico; en producción usar migraciones (Flyway/Liquibase) |
| 4 | **Sin pasarela de pago, sin correo/SMS** | Fuera del alcance de la especificación |
| 5 | **Secretos de desarrollo** | `JWT_SECRET` y las contraseñas de Compose son valores de desarrollo; cámbielos fuera de local |
| 6 | **Datos de demostración** | `DatabaseSeeder` crea un usuario por rol con contraseña `Password123*`; deshabilitar con `SEED_ENABLED=false` |
| 7 | **Coerción de tipos de Jackson** | No se fijó la propiedad de coerción escalar porque su nombre en Jackson 3 no pudo verificarse; sí están activos `fail-on-unknown-properties` y `fail-on-null-for-primitives` |
| 8 | **Un supervisor autenticado puede invocar el servicio de consulta de usuarios compartido con ADMIN** | Los servicios de consulta admiten ADMIN y SUPERVISOR; la separación por rol la hace la ruta (`/supervisor/**` vs `/admin/**`) y los servicios de escritura |

## 4. Qué se verificó y qué no

Ver el apartado «Estado de validación» del `README.md` del repositorio.
