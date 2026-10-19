# Capa: infraestructura

**Paquete:** `application.infrastructure` y clases de arranque. Sin lógica de negocio.

## 1. `NexusMarketApplication`

`@SpringBootApplication` en `application`. Arranca el contexto.

## 2. `infrastructure.config.DomainServicesConfig`

Registra como beans los servicios de dominio mediante `@ComponentScan` con filtro `REGEX ".*Service"` sobre `application.domain.services` y declara el bean `Clock` (`Clock.systemDefaultZone()`). Así el dominio se mantiene libre de anotaciones de Spring.

## 3. `infrastructure.seed.DatabaseSeeder`

`ApplicationRunner` **idempotente** (controlado por `nexusmarket.seed.enabled`, `SEED_ENABLED`).

| Qué crea | Cómo |
|---|---|
| Administrador inicial | Directo con `UserRepositoryPort` (aún no existe quien lo incorpore) |
| Supervisor, operador logístico | `AdminPort.registerStaffUser` |
| Bodega del marketplace | `AdminPort.createWarehouse` |
| Vendedor + primera bodega + usuario | `AdminPort.registerSeller` + `registerSellerUser` |
| Comprador + usuario | `PublicAccessPort.registerBuyer` + `registerBuyerUser` |
| Producto de demostración (2 variantes, 25 unidades c/u) | `SellerPort` |

Todos los usuarios de demostración usan la contraseña `nexusmarket.seed.password` (por defecto `Password123*`). **Deshabilítelo en producción.**

Usuarios: `admin@nexusmarket.com`, `supervisor@nexusmarket.com`, `operator@nexusmarket.com`, `seller@nexusmarket.com`, `buyer@nexusmarket.com`.

## 4. `adapters.notification.NoOpNotificationAdapter`

Implementa `NotificationPort` sin enviar nada (registra la intención). Es el punto de extensión para correo/SMS/push reales.
