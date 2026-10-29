# TecnoUAM · Sistema de préstamo de equipos tecnológicos

Examen práctico integrador — Servicios Web (segundo corte), UAM.

Aplicación cliente-servidor:

- **backend/** — API REST con **Spring Boot 4.1.2 (Java 21)**, Spring Data JPA, **PostgreSQL**,
  Spring Security y **JWT** (HS256, sin sesiones).
- **frontend/** — Interfaz **React 19 + Vite + Axios** (componentes funcionales, `useState`, `useEffect`).
- **postman/** — Colección exportada con todas las pruebas del Reto 4 (con tests automáticos).

## Integrantes

| # | Integrante | Responsabilidad principal |
|---|------------|---------------------------|
| 1 | _(nombre)_ | Entidades, relaciones JPA y base de datos |
| 2 | _(nombre)_ | CRUD, controladores y servicios |
| 3 | _(nombre)_ | Spring Security, JWT y roles |
| 4 | _(nombre)_ | React, Axios y visualización |
| 5 | _(nombre)_ | Pruebas Postman, integración y evidencias |

## Requisitos

- JDK **21**
- **PostgreSQL** (probado con la versión 18)
- **Node.js** 20+ (probado con 24)
- No hace falta instalar Maven: se usa el wrapper `mvnw` incluido.

## 1. Base de datos

Crear la base una sola vez (pgAdmin o psql):

```sql
CREATE DATABASE prestamos_equipos;
```

Las tablas y los datos mínimos se cargan **solos al arrancar** el backend con los scripts:

- `backend/src/main/resources/schema.sql` — tablas `usuarios`, `equipos`, `prestamos`, restricciones
  (`UNIQUE` del código patrimonial, `CHECK` de fechas y estados, índice único que impide dos préstamos
  activos del mismo equipo).
- `backend/src/main/resources/data.sql` — cuentas de prueba y 5 equipos iniciales (solo si la tabla está vacía).

Hibernate está en modo `validate`: comprueba que las entidades coinciden con las tablas, no las modifica.

## 2. Variables de entorno (backend)

Los secretos **no están en el repositorio**. `application.properties` los lee del entorno o de un archivo
`backend/.env` (incluido en `.gitignore`). Copia la plantilla y complétala:

```powershell
cd backend
copy .env.example .env
```

| Variable | Obligatoria | Descripción |
|----------|-------------|-------------|
| `DB_URL` | no | Por defecto `jdbc:postgresql://localhost:5432/prestamos_equipos` |
| `DB_USERNAME` | no | Por defecto `postgres` |
| `DB_PASSWORD` | **sí** | Contraseña de PostgreSQL |
| `JWT_SECRET` | **sí** | Clave HMAC para firmar los JWT, mínimo 32 caracteres |
| `JWT_EXPIRACION_MINUTOS` | no | Por defecto `60` |
| `CORS_ORIGENES` | no | Por defecto `http://localhost:5173` |

Generar un `JWT_SECRET` aleatorio en PowerShell:

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Max 256 }))
```

## 3. Ejecución

**Backend** (puerto 8080):

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**Frontend** (puerto 5173):

```powershell
cd frontend
npm install
npm run dev
```

Abrir http://localhost:5173 e iniciar sesión con una cuenta de prueba.

**Tests del backend** (usan H2 en memoria, no necesitan PostgreSQL):

```powershell
cd backend
.\mvnw.cmd test
```

## Cuentas de prueba

| Usuario | Contraseña | Rol | Permisos |
|---------|-----------|-----|----------|
| `admin` | `admin123` | ADMIN | Consultar, crear/actualizar/eliminar equipos, registrar préstamos y devoluciones |
| `instructor` | `user123` | USER | Solo consultar equipos y préstamos |

## Endpoints

| Método | Ruta | Rol | Respuestas |
|--------|------|-----|-----------|
| POST | `/api/auth/login` | público | 200 token · 400 · 401 credenciales incorrectas |
| GET | `/api/equipos` | USER, ADMIN | 200 |
| GET | `/api/equipos/{id}` | USER, ADMIN | 200 · 404 |
| POST | `/api/equipos` | ADMIN | 201 · 400 · 409 código duplicado |
| PUT | `/api/equipos/{id}` | ADMIN | 200 · 400 · 404 · 409 |
| DELETE | `/api/equipos/{id}` | ADMIN | 204 · 404 · 409 préstamo activo |
| GET | `/api/prestamos` (opcional `?estado=ACTIVO`) | USER, ADMIN | 200 |
| GET | `/api/prestamos/{id}` | USER, ADMIN | 200 · 404 |
| POST | `/api/prestamos` | ADMIN | 201 · 400 fechas/datos · 404 equipo · 409 equipo ya PRESTADO |
| PUT | `/api/prestamos/{id}/devolver` | ADMIN | 200 · 404 · 409 ya devuelto |

Sin token o con token inválido/expirado → **401**. Token válido sin el rol requerido → **403**.

Ejemplos de cuerpo:

```json
// POST /api/auth/login
{ "username": "admin", "password": "admin123" }

// POST/PUT /api/equipos   (tipo: PORTATIL | PROYECTOR | TABLETA | OTRO)
{ "codigoPatrimonial": "UAM-LAP-010", "nombre": "Laptop Dell Latitude", "tipo": "PORTATIL" }

// POST /api/prestamos   (fechaPrestamo es opcional: por defecto hoy)
{ "equipoId": 1, "nombreSolicitante": "Ing. Carlos Méndez",
  "fechaPrestamo": "2026-10-28", "fechaPrevistaDevolucion": "2026-11-04" }
```

Las peticiones protegidas llevan el encabezado `Authorization: Bearer <token>`.

## Reglas de negocio implementadas

| Regla | Dónde |
|-------|-------|
| Código patrimonial único | `EquipoService` (409) + `UNIQUE` en BD |
| Equipo nace `DISPONIBLE`; el estado no se envía, lo controla el sistema | `Equipo`, `EquipoRequest` |
| No prestar un equipo `PRESTADO` | `PrestamoService.registrar` (409) + índice único parcial en BD |
| Prestar → equipo `PRESTADO`; devolver → `DISPONIBLE` (misma transacción) | `PrestamoService` (`@Transactional`) |
| No eliminar equipo con préstamo activo | `EquipoService.eliminar` (409) |
| Campos obligatorios validados | Bean Validation en los DTO (400) |
| Fecha prevista ≥ fecha de préstamo | `PrestamoService` (400) + `CHECK` en BD |
| USER solo consulta; ADMIN modifica | `SecurityConfig` (403) |

## Estructura del backend

```
src/main/java/com/universidad/apiprestamos/
├── ApiPrestamosApplication.java
├── controller/   EquipoController, PrestamoController, AuthController
├── dto/          EquipoRequest/Response, PrestamoRequest/Response, LoginRequest/Response
├── exception/    RecursoNoEncontrado, ConflictoNegocio, DatosInvalidos, GlobalExceptionHandler
├── model/        Equipo, Prestamo (@ManyToOne ↔ @OneToMany), Usuario y enums
├── repository/   EquipoRepository, PrestamoRepository, UsuarioRepository
├── security/     SecurityConfig, JwtService, UsuarioDetailsService, respuestas 401/403
└── service/      EquipoService, PrestamoService (reglas de negocio)
src/main/resources/
├── application.properties
├── schema.sql
└── data.sql
```

**Relación JPA:** `Prestamo` es el lado dueño (`@ManyToOne` con la FK `equipo_id`); `Equipo` tiene el lado
inverso (`@OneToMany(mappedBy = "equipo")`). Un equipo puede tener muchos préstamos en su historial, pero
solo uno `ACTIVO` a la vez. Al eliminar un equipo sin préstamos activos se borra su historial (`CascadeType.REMOVE`).

**Flujo JWT:**
1. `POST /api/auth/login` → `AuthenticationManager` compara la contraseña con el hash BCrypt de la tabla `usuarios`.
2. Si es correcta, `JwtService` firma un token HS256 con `sub` (usuario), `roles` y expiración.
3. El cliente envía `Authorization: Bearer <token>` en cada petición.
4. El *resource server* de Spring Security verifica firma y expiración, y convierte el claim `roles` en
   `ROLE_ADMIN` / `ROLE_USER`. No hay sesión en el servidor (`STATELESS`).

**401 vs 403:** 401 = el servidor no sabe quién eres (sin token, token falso o vencido, credenciales
incorrectas). 403 = sabe quién eres, pero tu rol no tiene permiso.

## Frontend

- `services/api.js` — instancia de Axios; un interceptor agrega el token a cada petición.
- `hooks/useConsulta.js` — `useState` + `useEffect`: carga al montar, expone `cargando`, `error`, `datos`, `recargar`.
- `components/TablaEquipos.jsx` — tabla con código, nombre, tipo y estado; estados de carga, lista vacía y error.
- `components/TablaPrestamos.jsx` — historial de préstamos.
- Si la API responde 401 (token vencido) se cierra la sesión y vuelve al login.

Todos los datos vienen de la API (no hay datos simulados). Al crear o prestar desde Postman, pulsar
**Recargar** (o F5) muestra el cambio.

## Pruebas con Postman

Importar `postman/TecnoUAM-Prestamos.postman_collection.json` y ejecutar las carpetas en orden
(o **Run collection**). Los tokens e ids se guardan solos en variables de la colección.

| Carpeta | Demuestra |
|---------|-----------|
| 0. Autenticación | Login ADMIN, login USER, login incorrecto (401) |
| 1. Seguridad | Sin token (401), token inválido (401), USER consulta (200), USER crea (403) |
| 2. CRUD de equipos | (1) alta 201, duplicado 409, inválido 400, (2) consulta, 404, (3) actualización |
| 3. Préstamos | fecha inválida 400, (4) préstamo, (5) re-préstamo 409, (7b) eliminar bloqueado 409, (6) devolución y DISPONIBLE, (7a) eliminación 204 |
| 4. Demo React | Crea y presta un equipo; luego recargar la página de React (9) |

Las capturas de evidencia van en la carpeta `evidencias/`.
