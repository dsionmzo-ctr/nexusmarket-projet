# Capa: adaptadores de persistencia

**Paquete:** `application.adapters.persistence` — adaptadores de **salida** hacia PostgreSQL (JPA) y MongoDB.

## JPA (PostgreSQL)

| Subpaquete | Cantidad | Contenido |
|---|---|---|
| `entities` | 19 | `@Entity` por tabla + `AddressEmbeddable` |
| `mappers` | 16 | `toEntity` / `toDomain` / `ref` / `toShallowDomain` |
| `repositories` | 14 | Interfaces Spring Data con consultas derivadas |
| `adapters` | 14 | `@Repository @Transactional`, implementan los puertos de salida |

## MongoDB (auditoría)

`AuditLogDocument` → `AuditLogMongoMapper` → `AuditLogMongoRepository` → `AuditLogMongoAdapter` (`AuditLogRepositoryPort`).

## Reglas

- El dominio recibe y devuelve **modelos**; las entidades y documentos no salen de esta capa.
- Los catálogos se guardan por `code`; las relaciones entre agregados se reconstruyen como referencias ligeras (solo id).
- Sin SQL nativo: consultas derivadas.
- Auditoría solo-agregar.

Detalle completo (tablas, columnas, mapa puerto→adaptador→repositorio): `Adapters/Persistence-adapters.md`.
