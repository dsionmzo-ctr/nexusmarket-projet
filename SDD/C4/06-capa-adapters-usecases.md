# Capa: adaptadores de casos de uso

**Paquete:** `application.adapters.useCases` — implementan los **puertos de entrada**.

| Clase | Puerto que implementa | Rol |
|---|---|---|
| `PublicAccessUseCaseImpl` | `PublicAccessPort` | Público |
| `BuyerUseCaseImpl` | `BuyerPort` | `BUYER` |
| `SellerUseCaseImpl` | `SellerPort` | `SELLER` |
| `LogisticOperatorUseCaseImpl` | `LogisticOperatorPort` | `LOGISTIC_OPERATOR` |
| `AdminUseCaseImpl` | `AdminPort` | `ADMIN` |
| `SupervisorUseCaseImpl` | `SupervisorPort` | `SUPERVISOR` |

## Responsabilidades

- `@Service @Transactional`: una **transacción por caso de uso**.
- Inyectar por constructor los servicios de dominio y **delegar** cada método en el que resuelve ese caso de uso.
- Ningún `if` de negocio ni de autorización.

## Atomicidad

Los cambios de negocio y la `Operation` (PostgreSQL) quedan en la misma transacción. El `AuditLog` (MongoDB) es otro almacén y **no participa** de ella (ver `Adapters/Persistence-adapters.md`).

Mapeo método → servicio: `Adapters/Use-cases-adapters.md`.
