# Capa: dominio

**Paquete:** `application.domain` — el núcleo del negocio, **sin dependencias de framework**.

## Contenido

| Subpaquete | Cantidad | Contenido |
|---|---|---|
| `models` | 26 | Modelos ricos (`Order`, `Inventory`, `Product`…) |
| `valueobjects` | 20 | 17 catálogos de negocio + `DomainCatalog` + `Money` + `Address` |
| `enums` | 3 | Enumeraciones técnicas |
| `exceptions` | 21 | `DomainException` y subclases |
| `ports.in` | 6 | Un puerto por rol + acceso público |
| `ports.out` | 20 | Contratos hacia persistencia, seguridad, configuración y notificación |
| `services` | 105 | Un servicio por caso de uso, en 15 subpaquetes |

## Reglas

1. Los servicios son clases Java **planas**; se registran como beans desde `infrastructure.config.DomainServicesConfig`.
2. **Un servicio = una responsabilidad**, con un único método `execute`.
3. Los servicios reciben y devuelven **modelos**, nunca DTO ni ids sueltos.
4. Toda regla vive en un modelo (invariantes, transiciones) o en un servicio; ninguna en un adaptador.
5. El reloj se inyecta (`Clock`).
6. Ninguna clase del dominio importa `org.springframework`, `jakarta` ni `javax` (verificado).

## Subdominios de servicios

authorization (9) · billing (2) · buyer (6) · cart (6) · catalog (12) · inventory (13) · notification (1) · operation (9 servicios + `AuditDetails`) · order (10) · report (1) · returns (12) · seller (3) · shipping (6) · user (9) · warehouse (6).

Documentación: `domain/Domain Model.md`, `domain/Domain Value Objects.md`, `domain/Domain Services.md`, `domain/services/*.md`, `domain/Input-ports.md`, `domain/Output-ports.md`, `domain/Domain Exceptions.md`.
