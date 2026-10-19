# Servicios de Usuarios y autenticación

## Introducción

Dominio 1 (Administración de Usuarios) y la base de autenticación del marketplace: registro de usuarios de cuenta, login, logout, cambio de estado y carga del usuario autoritativo a partir de un token.

Cada servicio es una clase de dominio **sin anotaciones de framework**, con un único método público `execute(...)` y una sola razón para cambiar (Principio de Responsabilidad Única). Los servicios reciben y devuelven **modelos de dominio** (nunca DTOs ni identificadores primitivos sueltos) y se componen entre sí por inyección de constructor; los adaptadores `UseCaseImpl` solo los orquestan.

Número de servicios en esta área: **9**.

---

## ChangeUserStatusService

Cambia el estado de acceso de un usuario (ACTIVO, INACTIVO, BLOQUEADO). Solo el ADMIN puede hacerlo. La versión del token se incrementa, de modo que las sesiones del usuario afectado se cierran.

```java
User execute(User admin, User target, UserStatus newStatus)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `UserRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `InvalidUserException` |
| Operaciones auditadas | `USER_STATUS_CHANGE` |

---

## ConsultUsersService

Consulta los usuarios de la plataforma (ADMIN y SUPERVISOR).

```java
List<User> execute(User requester)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN`, `SUPERVISOR` |
| Servicios colaboradores | `AuthorizeOperationService` |
| Puertos de salida | `UserRepositoryPort` |
| Excepciones de dominio | — |
| Operaciones auditadas | — |

---

## LoadAuthenticatedUserService

Carga el usuario autoritativo vigente de un token (sub = id inmutable del usuario). Rechaza usuarios desconocidos, inactivos o bloqueados y tokens cuya versión difiere de authTokenVersion persistida. El rol y la asociación comprador/vendedor vienen del usuario cargado, nunca de claims del token.

```java
User execute(Long userId, long tokenVersion)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `UserRepositoryPort` |
| Excepciones de dominio | `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## LoginService

Autentica a un usuario del sistema por correo y contraseña y emite el token de acceso. Un correo desconocido y una contraseña errónea producen el mismo error. Flujo: buscar el usuario, validar la contraseña, validar el estado del usuario y generar el token mediante los puertos de salida.

```java
AuthenticationResult execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `UserRepositoryPort`, `PasswordServicePort`, `JwtServicePort` |
| Excepciones de dominio | `InvalidCredentialsException`, `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## LogoutService

Cierra la sesión de un usuario: se incrementa su authTokenVersion, de modo que todo token emitido antes es rechazado desde ese momento.

```java
void execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | `UserRepositoryPort` |
| Excepciones de dominio | `EntityNotFoundException`, `UnauthorizedOperationException` |
| Operaciones auditadas | — |

---

## RegisterBuyerUserService

Crea el usuario del sistema (credenciales) de un comprador ya registrado. La identidad y el rol se toman del comprador; el correo y la identificación deben ser únicos en la plataforma.

```java
User execute(User user)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `COMPRADOR` |
| Servicios colaboradores | `ValidatePasswordPolicyService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `BuyerRepositoryPort`, `UserRepositoryPort`, `PasswordServicePort` |
| Excepciones de dominio | `DuplicateResourceException`, `EntityNotFoundException`, `InvalidUserException` |
| Operaciones auditadas | `BUYER_REGISTRATION` |

---

## RegisterSellerUserService

Crea el usuario del sistema (credenciales) de un vendedor ya registrado. Solo el ADMIN puede hacerlo.

```java
User execute(User admin, User sellerUser)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `ValidatePasswordPolicyService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `SellerRepositoryPort`, `UserRepositoryPort`, `PasswordServicePort` |
| Excepciones de dominio | `DuplicateResourceException`, `EntityNotFoundException`, `InvalidUserException` |
| Operaciones auditadas | `USER_REGISTRATION` |

---

## RegisterStaffUserService

Crea un usuario OPERADOR LOGÍSTICO, SUPERVISOR o ADMIN. Solo el ADMIN puede hacerlo.

```java
User execute(User admin, User newUser)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | `ADMIN` |
| Servicios colaboradores | `AuthorizeOperationService`, `ValidatePasswordPolicyService`, `RegisterOperationAndAuditService` |
| Puertos de salida | `UserRepositoryPort`, `PasswordServicePort` |
| Excepciones de dominio | `DuplicateResourceException`, `InvalidUserException` |
| Operaciones auditadas | `USER_REGISTRATION` |

---

## ValidatePasswordPolicyService

Valida la política de contraseñas: de 8 a 100 caracteres con al menos una letra y un dígito.

```java
void execute(String rawPassword)
```

| Aspecto | Detalle |
|---|---|
| Roles autorizados (directos) | — |
| Servicios colaboradores | — |
| Puertos de salida | — |
| Excepciones de dominio | `InvalidUserException` |
| Operaciones auditadas | — |

---
