# C4 Nivel 3 — Componentes

## Propósito

Descomponer el contenedor `nexusmarket-app` en sus componentes internos y mostrar el ciclo de vida de una petición.

## Diagrama

```mermaid
C4Component
    title Componentes — nexusmarket-app

    Container_Boundary(app, "nexusmarket-app") {
        Component(filters, "Filtros de seguridad", "RequestIdFilter, CORS, JwtAuthenticationFilter", "Correlación, token, usuario autoritativo")
        Component(rest, "Adaptadores REST", "7 controladores + DTO + mappers", "HTTP ↔ puertos de entrada")
        Component(errors, "GlobalExceptionHandler", "@RestControllerAdvice", "Sobre de error estándar")
        Component(usecases, "Adaptadores de casos de uso", "6 *UseCaseImpl", "Implementan puertos in; frontera transaccional")
        Component(domain, "Dominio", "105 servicios + 26 modelos + value objects", "Reglas de negocio puras")
        Component(portsout, "Puertos de salida", "20 interfaces", "Contratos hacia el exterior")
        Component(jpa, "Persistencia JPA", "entidades, mappers, repositorios, adaptadores", "PostgreSQL")
        Component(mongo, "Persistencia MongoDB", "documento, mapper, repositorio, adaptador", "Auditoría")
        Component(sec, "Adaptadores de seguridad", "JwtProvider, BCrypt, RoleAuthorizationAdapter", "JWT y contraseñas")
        Component(infra, "Infraestructura", "DomainServicesConfig, DatabaseSeeder", "Ensamblado y datos iniciales")
    }

    Rel(filters, rest, "principal autenticado")
    Rel(rest, usecases, "puertos de entrada")
    Rel(rest, errors, "excepciones")
    Rel(usecases, domain, "execute(...)")
    Rel(domain, portsout, "depende de")
    Rel(jpa, portsout, "implementa")
    Rel(mongo, portsout, "implementa")
    Rel(sec, portsout, "implementa")
    Rel(infra, domain, "registra servicios")
```

## Ciclo de vida de una petición

```mermaid
sequenceDiagram
    participant Cl as Cliente
    participant RID as RequestIdFilter
    participant CORS as CORS
    participant JWT as JwtAuthenticationFilter
    participant SC as SecurityConfig (ruta/rol)
    participant Ctl as Controlador
    participant Map as Mapper
    participant UC as UseCaseImpl (@Transactional)
    participant Svc as Servicio de dominio
    participant Aut as AuthorizeOperationService
    participant Rep as Puertos de salida
    participant Aud as RegisterOperationAndAuditService
    Cl->>RID: petición HTTP
    RID->>CORS: X-Request-Id asignado
    CORS->>JWT: origen permitido
    JWT->>JWT: valida firma/exp, carga usuario (sub, ver)
    JWT->>SC: contexto con AuthenticatedUserPrincipal
    SC->>Ctl: ruta y rol autorizados
    Ctl->>Ctl: @Valid (formato)
    Ctl->>Map: DTO → modelo
    Ctl->>UC: puerto.caso(user, modelo)
    UC->>Svc: execute(user, modelo)
    Svc->>Aut: usuario ACTIVO + rol permitido (RG-01/02/03)
    Svc->>Rep: lee / valida invariantes / guarda
    Svc->>Aud: Operation (PostgreSQL) + AuditLog (MongoDB)
    Svc-->>UC: modelo
    UC-->>Ctl: modelo
    Ctl->>Map: modelo → DTO
    Ctl-->>Cl: respuesta JSON (+ X-Request-Id)
```

Cualquier excepción en `Ctl`, `UC` o `Svc` la captura `GlobalExceptionHandler`; los fallos de `JWT`/`SC` los escribe `SecurityErrorResponseWriter` con el mismo sobre.
