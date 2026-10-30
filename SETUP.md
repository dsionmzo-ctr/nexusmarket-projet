# Guía paso a paso: poner en marcha NexusMarket

Todos los comandos se ejecutan desde la raíz del repositorio, salvo indicación contraria. Los ejemplos de `curl` usan Bash (Linux/macOS/Git Bash); en PowerShell use `curl.exe` y archivos para los cuerpos JSON (`-d "@archivo.json"`).

## 0. Prerrequisitos

1. **Docker** y **Docker Compose** (`docker --version`, `docker compose version`). Java y Maven corren dentro del contenedor, así que no es obligatorio instalarlos.
2. Puertos libres: **5432** (PostgreSQL), **27017** (MongoDB) y **8080** (app). Si el 8080 está ocupado: `HOST_SERVER_PORT=8081 docker compose up -d --build`.
3. *(Opcional, para ejecutar sin Docker)* JDK 25 (LTS), Maven 3.9, PostgreSQL 16 y MongoDB 6 locales. El usuario PostgreSQL configurado debe tener permiso `CREATEDB` para que la aplicación genere automáticamente la base de datos si no existe.

## 1. Levantar la aplicación y las bases de datos

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f nexusmarket-app
```

Esto crea PostgreSQL (`NexusMarket`) y MongoDB (`audit_db`) y arranca la API en `http://localhost:8080`. Hibernate crea las tablas al iniciar; MongoDB crea `audit_logs` con el primer evento. La primera vez Maven descarga dependencias (varios minutos).

Verificar salud:

```bash
curl -s http://localhost:8080/actuator/health
# {"status":"UP"}
```

## 2. Compilar y correr las pruebas

```bash
docker compose exec nexusmarket-app mvn -B test
```

Resultado esperado: `BUILD SUCCESS`. Las pruebas de dominio no requieren bases de datos.

Sin Docker: `cd nexusmarket && mvn test`.

Al iniciar, la aplicación comprueba la existencia de la base indicada por `DB_URL` y la crea si hace falta, antes de inicializar JPA. Para omitir este comportamiento (por ejemplo, cuando la base la administra un servicio externo), configura `DB_CREATE_DATABASE=false` y aprovisiona la base previamente. `spring.jpa.hibernate.ddl-auto=update` crea o actualiza las tablas una vez que la base está disponible.

## 3. Ejecutar la aplicación directamente (sin Docker)

```bash
# Con PostgreSQL y MongoDB locales en marcha:
cd nexusmarket
export DB_URL=jdbc:postgresql://localhost:5432/NexusMarket DB_USER=postgres DB_PASSWORD=postgres
export MONGODB_URI=mongodb://localhost:27017/audit_db
mvn spring-boot:run
```

Cree antes la base: `createdb -U postgres NexusMarket`.

## 4. Prueba de humo end-to-end

Con `SEED_ENABLED=true` (por defecto) existen usuarios de demostración (contraseña `Password123*`).

1. **Catálogo público** (sin token) → 200:
   ```bash
   curl -s http://localhost:8080/api/v1/catalog/products
   ```
2. **Registrar un comprador** (perfil + cuenta de acceso con contraseña) → 201:
   ```bash
   curl -s -X POST http://localhost:8080/api/v1/auth/register/buyer -H "Content-Type: application/json" \
     -d '{"identification":"1017123456","name":"Juan Perez","email":"juan.perez@example.com","password":"SecurePass123","primaryAddress":{"street":"Calle 50 40-20","city":"Medellin","country":"Colombia"}}'
   ```
3. *(Opcional, solo para compradores registrados sin cuenta)* **Crear su usuario** → 201:
   ```bash
   curl -s -X POST http://localhost:8080/api/v1/auth/register/user -H "Content-Type: application/json" \
     -d '{"buyerIdentification":"1017123456","password":"SecurePass123"}'
   ```
4. **Login** → 200 con `token`, `tokenType`, `expiresIn` y `user`:
   ```bash
   TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" \
     -d '{"email":"juan.perez@example.com","password":"SecurePass123"}' | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")
   ```
5. **Endpoint protegido con token** → 200 (sin token → **401**):
   ```bash
   curl -s -w "\nHTTP:%{http_code}\n" http://localhost:8080/api/v1/buyer/profile -H "Authorization: Bearer $TOKEN"
   ```
6. **Rol equivocado** → **403**:
   ```bash
   curl -s -w "\nHTTP:%{http_code}\n" http://localhost:8080/api/v1/admin/users -H "Authorization: Bearer $TOKEN"
   ```
7. **Flujo de compra** (use el `variantId` que devuelve el catálogo):
   ```bash
   curl -s -X POST http://localhost:8080/api/v1/buyer/cart/items -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"variantId":1,"quantity":2}'
   curl -s -X POST http://localhost:8080/api/v1/buyer/orders -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{}'
   curl -s -X PATCH http://localhost:8080/api/v1/buyer/orders/1/payment -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"paymentMethod":"CREDIT_CARD"}'
   curl -s http://localhost:8080/api/v1/buyer/orders/1/invoice -H "Authorization: Bearer $TOKEN"
   ```
   Luego, como `operator@nexusmarket.com`, despache y entregue el envío (`PATCH /api/v1/logistic-operator/shipments/{id}/dispatch` y `/delivery`).
8. **Logout** → 204; el token viejo queda invalidado → **401**:
   ```bash
   curl -s -o /dev/null -w "HTTP:%{http_code}\n" -X POST http://localhost:8080/api/v1/auth/logout -H "Authorization: Bearer $TOKEN"
   curl -s -o /dev/null -w "HTTP:%{http_code}\n" http://localhost:8080/api/v1/buyer/profile -H "Authorization: Bearer $TOKEN"
   ```
9. **Verificar persistencia:**
   ```bash
   docker exec nexusmarket-postgres psql -U postgres -d NexusMarket -c "\dt"
   docker exec nexusmarket-postgres psql -U postgres -d NexusMarket -c "select id,email,role,status,auth_token_version from users;"
   docker exec nexusmarket-mongo mongosh --quiet audit_db --eval "db.audit_logs.countDocuments()"
   ```

La lista completa de endpoints está en `SDD/Adapters/Api-rest-endpoints.md`.

## 5. Apagar todo

```bash
docker compose down        # conserva los volúmenes con datos
docker compose down -v     # borra también los datos
```

## 6. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Port 8080 was already in use` | Otro proceso ocupa el puerto | `HOST_SERVER_PORT=8081 docker compose up -d --build` |
| `Connection refused` a PostgreSQL | Base apagada o aún iniciando | `docker compose up -d` y esperar al *healthcheck* |
| Auditoría no aparece en MongoDB | Otro `mongod` nativo ocupa 27017 | Detenerlo o cambiar `MONGODB_URI` |
| La app falla con `jwt.secret` | El secreto tiene menos de 32 bytes | Definir `JWT_SECRET` de al menos 32 caracteres |
| La app falla con `FRONTEND_ORIGIN` | Se configuró `*` | Usar un origen explícito (el comodín está prohibido) |
| Errores de esquema tras cambiar modelos | Esquema viejo en PostgreSQL | Solo en desarrollo: `docker compose down -v` y volver a levantar |
| `400` con JSON inline en PowerShell | Comillas mutiladas | Usar `-d "@archivo.json"` |

## Ejecutar desde VS Code (sin contenedor para la API)

Si lanzas `NexusMarketApplication` con el botón *Run* de VS Code, la API corre en tu máquina y necesita PostgreSQL y MongoDB **ya levantados** en `localhost`. Si no, el arranque termina con `Connection to localhost:5432 refused` y `Unable to determine Dialect without JDBC metadata`.

1. Abre Docker Desktop y espera a que esté en verde.
2. Levanta solo las bases de datos:
   ```bash
   docker compose up -d postgres-db mongo-db
   ```
3. Comprueba que estén `healthy` con `docker compose ps`.
4. Ejecuta `NexusMarketApplication` desde VS Code (usa por defecto `localhost:5432`, usuario y clave `postgres`, y `localhost:27017`).

Los mensajes `Could not safely identify store assignment for repository candidate...` eran avisos informativos de Spring Data (hay JPA y Mongo juntos); no son errores y ya están ocultos.
