# NexusMarket — Construcción de Software 2 (2026-2)

Marketplace en **Java 25 (LTS) + Spring Boot 4.1** con **arquitectura hexagonal** (DDD + Ports & Adapters), construido a partir de la especificación funcional de NexusMarket y del `SDD/` del proyecto de referencia. Incluye 5 roles (Comprador, Vendedor, Operador Logístico, Administrador, Supervisor), JWT con invalidación de sesiones, persistencia relacional en **PostgreSQL** y auditoría en **MongoDB**.

> **Para ponerlo en marcha paso a paso, ver [`SETUP.md`](SETUP.md).**
> **Para entender qué se tomó del ejemplo del banco, ver [`SDD/Mapeo-banco-a-nexus.md`](SDD/Mapeo-banco-a-nexus.md).**
> **Supuestos y vacíos de la especificación:** [`SDD/Supuestos-y-vacios.md`](SDD/Supuestos-y-vacios.md).

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje / Build | Java 25 (LTS), Maven |
| Framework | Spring Boot 4.1.1, Spring MVC, Spring Security (JWT stateless, BCrypt) |
| Persistencia relacional | Spring Data JPA / Hibernate → PostgreSQL 16 (`NexusMarket`, puerto 5432, DDL automático) |
| Auditoría NoSQL | Spring Data MongoDB → MongoDB 6 (`audit_db.audit_logs`, puerto 27017) |
| JWT | JJWT 0.12.6 (`sub`, `jti`, `ver`, `iat`, `exp`, `iss`; sin datos personales) |
| Validación / errores | Jakarta Bean Validation, `@RestControllerAdvice` con sobre estándar + `X-Request-Id` |
| Observabilidad | Spring Boot Actuator (`/actuator/health`, `/actuator/info`) |
| Pruebas | JUnit 5 (`mvn test`) |

## Estructura del repositorio

```text
.
├── nexusmarket/             # Aplicación Spring Boot (código + tests + resources)
│   ├── Dockerfile
│   └── src/main/java/application/
│       ├── domain/          # Núcleo puro: models, valueobjects, enums, exceptions,
│       │                    #   ports/in (6), ports/out (20), services (105)
│       ├── adapters/        # rest, useCases, persistence (jpa, mongodb), security,
│       │                    #   config, notification
│       └── infrastructure/  # config (DomainServicesConfig), seed (DatabaseSeeder)
├── SDD/                     # Especificación en español (ver índice abajo)
├── docker-compose.yml       # App + PostgreSQL 5432 + MongoDB 27017
├── SETUP.md                 # Guía paso a paso
└── README.md
```

### Índice del SDD

| Carpeta / archivo | Contenido |
|---|---|
| `especificacion_funcional_nexusmarket.md` | Especificación funcional recibida |
| `enunciado.md` | Interpretación para el desarrollo (roles, información, flujos, bitácora, reglas) |
| `enunciado evaluativo.md` | Rúbrica de evaluación (del profesor) |
| `Orchestrator-prompt.md` | Prompt de orquestación por fases |
| `Software Architecture/` | Arquitectura hexagonal |
| `domain/` | Modelo, value objects, excepciones, servicios (`services/*.md`, 105 servicios), puertos de entrada y salida |
| `Adapters/` | REST, endpoints (81), validación, seguridad/CORS, manejador de errores, casos de uso, persistencia |
| `C4/` | Niveles 1–4, ficha por capa y flujos por endpoint (Mermaid) |
| `Mapeo-banco-a-nexus.md`, `Supuestos-y-vacios.md` | Guía de estudio y decisiones |

## Principio de Responsabilidad Única

Cada servicio de dominio resuelve **un único caso de uso** (`CheckoutService`, `PayOrderService`, `ReserveStockService`, `ApproveRefundService`…) con un solo método `execute`. Los adaptadores `*UseCaseImpl` solo los orquestan dentro de una transacción. Ver `SDD/domain/Domain Services.md`.

## Seguridad y roles

- JWT sin sesión. El token solo lleva identidad; **rol y estado se leen de la base de datos en cada petición**.
- Logout, cambio de estado o cambio relevante de seguridad incrementan `authTokenVersion` e **invalidan los tokens anteriores**.
- Rutas por rol: `/api/v1/buyer`, `/seller`, `/logistic-operator`, `/admin`, `/supervisor`, más público (`/api/v1/auth`, `/api/v1/catalog`).
- Sin token → 401; rol insuficiente → 403; 404/409 con códigos estables.
- CORS de un único origen (`FRONTEND_ORIGIN`, por defecto `http://localhost:5173`).

## Variables de entorno

| Variable | Default | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/NexusMarket` | JDBC PostgreSQL (en Compose: `postgres-db`) |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `postgres` | Credenciales PostgreSQL |
| `DB_CREATE_DATABASE` | `true` | Crea la base configurada si no existe; el usuario debe tener permiso `CREATEDB` |
| `MONGODB_URI` | `mongodb://localhost:27017/audit_db` | Auditoría |
| `JWT_SECRET` | secreto de desarrollo | Clave HS256 (mínimo 32 bytes; **cámbiela fuera de local**) |
| `JWT_EXPIRATION_MS` | `3600000` | Vigencia del token |
| `JWT_ISSUER` | `nexusmarket` | Emisor |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | Origen CORS permitido |
| `BUSINESS_TAX_RATE` | `0.19` | Impuesto de la factura |
| `BUSINESS_RETURN_WINDOW_DAYS` | `30` | Ventana de devolución |
| `SEED_ENABLED` / `SEED_PASSWORD` | `true` / `Password123*` | Datos de demostración |
| `SERVER_PORT` | `8080` | Puerto HTTP |

## Usuarios de demostración (si `SEED_ENABLED=true`)

Contraseña de todos: `Password123*`.

| Rol | Correo |
|---|---|
| Administrador | `admin@nexusmarket.com` |
| Supervisor | `supervisor@nexusmarket.com` |
| Operador logístico | `operator@nexusmarket.com` |
| Vendedor | `seller@nexusmarket.com` |
| Comprador | `buyer@nexusmarket.com` |

## Estado de validación

Lo que **sí** se verificó en el entorno de desarrollo del asistente (sin acceso a Maven Central):

- [x] Todo el código de dominio, casos de uso, persistencia, REST y utilidades compila con `javac` frente a *stubs* de las APIs de Spring/Jakarta/JPA/Mongo (verifica coherencia interna del código propio).
- [x] **67 pruebas** ejecutadas y en verde: flujo de autenticación, catálogo e inventario, ciclo completo del pedido, devoluciones y reembolsos, autorización por rol y auditoría, mapeo de errores. Se ejecutan sobre el dominio y los `UseCaseImpl` con repositorios en memoria.

Lo que **no** se pudo verificar y debe comprobar en su equipo (`mvn test`, `docker compose up`):

- [ ] `mvn package` con las dependencias reales (la compilación contra *stubs* no valida las firmas reales de Spring Boot 4, Hibernate, JJWT ni Spring Security).
- [ ] Arranque contra PostgreSQL y MongoDB reales (DDL, mapeos JPA, consultas derivadas).
- [ ] `SecurityConfig`, `JwtProvider`, filtros y `GlobalExceptionHandler` en ejecución (401/403/CORS).
- [ ] Prueba de humo end-to-end de `SETUP.md` §4.
