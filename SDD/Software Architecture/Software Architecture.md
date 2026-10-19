# Arquitectura de Software

## Visión general

NexusMarket sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con principios de **Domain-Driven Design (DDD)**.

El objetivo principal es **aislar el dominio del negocio de las tecnologías externas** (frameworks, bases de datos, protocolos de comunicación, seguridad), de modo que las reglas del marketplace —estados del pedido, inventario sin existencias negativas, autorización por rol— no dependan de Spring, JPA ni MongoDB. Esto facilita el mantenimiento, la prueba y el cambio de tecnología.

---

# Principios arquitectónicos

- Diseño centrado en el dominio.
- Separación de responsabilidades.
- Inversión de dependencias.
- Independencia tecnológica.
- Alta cohesión y bajo acoplamiento.
- Límites explícitos entre capas.
- **Principio de Responsabilidad Única (SRP)**: un servicio de dominio = un caso de uso o una regla reutilizable (105 servicios).

El dominio contiene todas las reglas de negocio y **nunca** depende de tecnologías externas.

---

# Capas de la arquitectura

```text
application
│
├── adapters          ← traducen entre el mundo exterior y el dominio
│   ├── rest          (entrada: HTTP/JSON)
│   ├── useCases      (entrada: implementan los puertos de entrada)
│   ├── persistence   (salida: jpa y mongodb)
│   ├── security      (salida/entrada: JWT, BCrypt, filtros)
│   ├── config        (salida: parámetros de negocio)
│   └── notification  (salida: notificaciones)
│
├── domain            ← el núcleo del negocio
│   ├── models  valueobjects  enums  exceptions
│   ├── services
│   └── ports (in / out)
│
└── infrastructure    ← ensamblado y datos de arranque
    ├── config
    └── seed
```

---

# Estructura de paquetes

```text
src/main/java/application
├── NexusMarketApplication.java
├── adapters
│   ├── rest
│   │   ├── controllers   (7)      Auth, PublicCatalog, Buyer, Seller, LogisticOperator, Admin, Supervisor
│   │   ├── dtos
│   │   │   ├── requests  (24)
│   │   │   └── responses (25)
│   │   ├── mappers       (17)
│   │   └── exception     ErrorCodeMapping, GlobalExceptionHandler
│   ├── useCases          (6)      PublicAccess, Buyer, Seller, LogisticOperator, Admin, Supervisor
│   ├── persistence
│   │   ├── jpa
│   │   │   ├── entities (19)  mappers (16)  repositories (14)  adapters (14)
│   │   └── mongodb
│   │       └── documents  mappers  repositories  adapters
│   ├── security              JwtProvider, JwtAuthenticationFilter, RequestIdFilter, SecurityConfig, …
│   ├── config                DefaultBusinessConfigurationAdapter
│   └── notification          NoOpNotificationAdapter
├── domain
│   ├── models        (26)
│   ├── valueobjects  (20)  catálogos de negocio + Money + Address
│   ├── enums         (3)   enumeraciones técnicas
│   ├── exceptions    (21)
│   ├── ports
│   │   ├── in        (6)   un puerto por rol + acceso público
│   │   └── out       (20)
│   └── services      (105, en 15 subpaquetes por subdominio)
└── infrastructure
    ├── config        DomainServicesConfig
    └── seed          DatabaseSeeder
```

---

# Responsabilidades por capa

## `NexusMarketApplication`

Punto de arranque Spring Boot. Solo ensambla el contexto; no contiene lógica.

## Adaptadores (`adapters/`)

Implementan los puertos y traducen tecnologías. **Nunca definen reglas de negocio.**

### Adaptadores REST (`adapters/rest/`)

- Reciben HTTP/JSON, validan formato con Jakarta Validation y convierten DTO ↔ modelos con mappers estáticos.
- Invocan **solo** los puertos de entrada con el `User` del token.
- Detalle en `Adapters/Rest-adapters.md`, `Adapters/Api-rest-endpoints.md`, `Adapters/Rest-validation.md`, `Adapters/Global-exception-handler.md`.

### Adaptadores de casos de uso (`adapters/useCases/`)

- Implementan los puertos de entrada (`BuyerPort`, `SellerPort`, `LogisticOperatorPort`, `AdminPort`, `SupervisorPort`, `PublicAccessPort`).
- `@Service @Transactional`; delegan cada método en el servicio de dominio correspondiente.
- Detalle en `Adapters/Use-cases-adapters.md`.

### Adaptadores de persistencia (`adapters/persistence/`)

- **JPA** (PostgreSQL): entidades, mappers, repositorios Spring Data y adaptadores de los puertos de repositorio.
- **MongoDB**: documento, mapper, repositorio y adaptador del puerto de auditoría.
- Detalle en `Adapters/Persistence-adapters.md`.

### Seguridad (`adapters/security/`)

- JWT stateless (JJWT), BCrypt, filtro de autenticación, `X-Request-Id`, CORS y manejo de 401/403.
- Detalle en `Adapters/Rest-security-cors.md`.

## Dominio (`domain/`)

El núcleo, sin dependencias de framework.

### Modelos

26 clases ricas (`Order`, `Inventory`, `Product`, `Shipment`, `Refund`…) con identidad, invariantes y transiciones de estado. Ver `domain/Domain Model.md`.

### Value Objects

Catálogos de negocio (`DomainCatalog`: `SystemRole`, `OrderStatus`, `ProductType`…), `Money` y `Address`. **No hay `String` abiertos en campos acotados.** Ver `domain/Domain Value Objects.md`.

### Enums

Solo conceptos técnicos (`AuditSeverity`, `NotificationChannel`, `ApprovalDecision`).

### Servicios

105 clases Java planas, **una por caso de uso**, con un único método `execute(...)`. Se agrupan en 15 subdominios. Ver `domain/Domain Services.md` y `domain/services/`.

### Puertos

- **De entrada (6):** qué casos de uso ofrece cada rol. Ver `domain/Input-ports.md`.
- **De salida (20):** qué necesita el dominio del exterior. Ver `domain/Output-ports.md`.

### Excepciones

21 excepciones de dominio (`DomainException` y subclases) con significado de negocio; el adaptador REST las traduce a HTTP.

## Infraestructura (`infrastructure/`)

- **`DomainServicesConfig`**: registra los servicios de dominio como beans (`@ComponentScan` con filtro de expresión regular `.*Service` sobre `application.domain.services`) y un `Clock`. Es lo que permite que el dominio no tenga anotaciones de Spring.
- **`DatabaseSeeder`**: `ApplicationRunner` idempotente que crea datos de demostración (un usuario por rol, bodega del marketplace y un producto con stock). El primer administrador se crea por el puerto de repositorio (no hay quien lo incorpore); todo lo demás pasa por los puertos de entrada, de modo que las reglas y la auditoría se aplican igual. Se desactiva con `SEED_ENABLED=false`.

---

# Flujo de dependencias

Las dependencias siempre apuntan hacia el dominio.

```text
Controlador REST
      │
      ▼
Puerto de entrada  ◀── implementado por ── UseCaseImpl
      │                                        │
      ▼                                        ▼
Servicio de dominio ────────────────▶ Puerto de salida
                                              ▲
                                              │ implementado por
                                     Adaptador de persistencia / seguridad / notificación
                                              │
                                              ▼
                                  PostgreSQL · MongoDB · BCrypt · JJWT
```

El dominio nunca depende de los adaptadores ni de la infraestructura.

---

# Seguridad en dos niveles

1. **Adaptador de seguridad:** autentica el token y autoriza por **ruta y rol** (`/api/v1/<rol>/**`).
2. **Dominio:** cada servicio vuelve a autorizar (`AuthorizeOperationService`, propiedad del recurso). Así el dominio es seguro aunque se invoque desde otro adaptador (RG-01, RG-02, RG-03).

---

# Trazabilidad

Cada servicio que cambia el estado del negocio llama a `RegisterOperationAndAuditService`, que persiste una `Operation` (PostgreSQL) y un `AuditLog` inmutable (MongoDB). Ver `domain/services/operation-audit-services.md`.

---

# Beneficios

- Independencia tecnológica: cambiar PostgreSQL por otra base solo toca los adaptadores JPA.
- Mantenibilidad y claridad: cada servicio tiene una razón para cambiar.
- Testabilidad: el dominio se prueba con repositorios en memoria, sin Spring ni base de datos (65 pruebas de dominio en `src/test`).
- Reutilización de la lógica de negocio por distintos adaptadores.
- Mejor soporte para DDD: lenguaje ubicuo, agregados e invariantes en los modelos.

---

# Restricciones arquitectónicas

Estas reglas deben respetarse siempre:

1. La lógica de negocio pertenece exclusivamente al dominio.
2. Los controladores no contienen reglas de negocio.
3. Los DTO nunca entran al dominio.
4. Las entidades de persistencia nunca se exponen por la API.
5. La comunicación entre tecnologías y dominio ocurre solo a través de puertos.
6. Los adaptadores implementan puertos pero nunca definen reglas de negocio.
7. La infraestructura depende del dominio, nunca al revés.
8. Toda dependencia apunta hacia el dominio.
9. Los modelos de negocio no dependen de frameworks.
10. Un servicio de dominio = una responsabilidad.
