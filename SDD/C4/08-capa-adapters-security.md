# Capa: adaptadores de seguridad

**Paquete:** `application.adapters.security` — JWT, filtros, contraseñas y autorización por ruta/rol.

## 1. Clases

| Clase | Responsabilidad |
|---|---|
| `JwtProvider` | Implementa `JwtServicePort`: emite y valida el token (HS256, claims `sub`, `jti`, `ver`, `iat`, `exp`, `iss`) |
| `JwtAuthenticationFilter` | Lee el `Bearer`, valida, carga el usuario y llena el contexto de seguridad |
| `SecurityUserLoader` | Puente entre el filtro y `LoadAuthenticatedUserService` |
| `dtos.AuthenticatedUserPrincipal` | Principal de Spring: contiene el `User` de dominio; autoridad `ROLE_<code>` |
| `dtos.JwtClaimsDTO` | Claims leídos del token |
| `mappers.JwtClaimsMapper` | Claims → DTO |
| `SecurityConfig` | Cadena de filtros, reglas por ruta/rol, CORS, `UserDetailsService` que siempre rechaza |
| `RequestIdFilter` | Acepta o genera `X-Request-Id` |
| `RestAuthenticationEntryPoint` / `RestAccessDeniedHandler` / `SecurityErrorResponseWriter` | 401/403/503 con el sobre estándar |
| `BCryptPasswordServiceAdapter` / `PasswordEncoderConfig` | Implementan `PasswordServicePort` con BCrypt |
| `RoleAuthorizationAdapter` | Implementa `AuthorizationPort` (información de autorización externa) |

## 2. Token

El token **no lleva datos personales, rol ni estado**; solo `sub`, `jti`, `ver` (versión de sesión), `iat`, `exp`, `iss`. El rol y el estado se leen del modelo vigente en cada petición. Cambiar de estado o cerrar sesión incrementa `authTokenVersion` y deja obsoletos los tokens anteriores.

## 3. Reglas de ruta

Ver la tabla completa en `Adapters/Rest-security-cors.md`.

## 4. Contraseñas

BCrypt; política de 8–100 caracteres con letra y dígito; la contraseña en claro nunca se persiste, devuelve ni audita.

## 5. CORS

Un único origen (`FRONTEND_ORIGIN`), sin comodín, `allowCredentials=false`.

## 6. Configuración de negocio

`adapters.config.DefaultBusinessConfigurationAdapter` implementa `BusinessConfigurationPort` con `nexusmarket.business.tax-rate` (0.19) y `nexusmarket.business.return-window-days` (30).
