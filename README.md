# API de Gestión de Eventos

API REST para la gestión de eventos, categorías y oradores, construida con **Spring Boot 4** y **Java 21**. Incluye autenticación con **JWT**, control de acceso por roles (`ADMIN` y `USER`), persistencia en **PostgreSQL**, documentación interactiva con **Swagger/OpenAPI** y despliegue con **Docker**.

## Contexto

Proyecto desarrollado con fines educativos, como trabajo práctico de un curso de [DevTalles](https://devtalles.com) en Udemy.

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.1 (Spring Web MVC) |
| Persistencia | Spring Data JPA, PostgreSQL (Neon), H2 |
| Seguridad | Spring Security, JWT (jjwt 0.13.0), BCrypt |
| Validación | Spring Boot Validation |
| Mapeo de objetos | MapStruct 1.6.3, Lombok |
| Documentación | springdoc-openapi 2.8.13 (Swagger UI) |
| Monitoreo | Spring Boot Actuator |
| Build | Maven (Maven Wrapper incluido) |
| Contenedores | Docker, Docker Compose |

## Funcionalidades

- CRUD de **eventos**, **categorías** y **oradores**.
- Listado de eventos con **paginación** y filtro por nombre.
- Registro e inicio de sesión de usuarios, con autenticación **stateless** mediante JWT.
- Autorización por rol a nivel de método (`ADMIN` y `USER`).
- Carga de **datos iniciales** al arrancar (roles, usuarios, categorías, oradores y eventos de ejemplo).
- Relaciones entre entidades: eventos con múltiples oradores y una categoría, y usuarios que asisten a eventos.
- Tres variantes de consulta de eventos que permiten comparar el problema **N+1** frente a su optimización con `JOIN FETCH`.
- Documentación interactiva con Swagger UI (perfil `dev`).
- Configuración por perfiles (`dev` y `prod`) y credenciales mediante variables de entorno.

## Requisitos previos

- JDK 21
- Docker y Docker Compose (opcional)
- Una base de datos PostgreSQL (el proyecto está configurado para [Neon](https://neon.tech))

No hace falta instalar Maven: se usa el wrapper (`./mvnw`).

## Configuración

La aplicación se configura con variables de entorno. Creá un archivo `.env` en la raíz del proyecto tomando como base este ejemplo:

```env
NEON_DB_URL=jdbc:postgresql://<host>/<base_de_datos>?sslmode=require
NEON_DB_USER=<usuario>
NEON_DB_PASSWORD=<contraseña>

JWT_SECRET=<clave_secreta_larga_y_aleatoria>
JWT_EXPIRATION=600000
```

| Variable | Descripción |
|---|---|
| `NEON_DB_URL` | URL JDBC de la base de datos PostgreSQL |
| `NEON_DB_USER` | Usuario de la base de datos |
| `NEON_DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave con la que se firman los tokens JWT |
| `JWT_EXPIRATION` | Duración del token en milisegundos (`600000` = 10 minutos) |
| `SPRING_PROFILES_ACTIVE` | Perfil activo: `dev` (por defecto) o `prod` |

> ⚠️ **Nunca subas el archivo `.env` al repositorio.** Está incluido en `.gitignore`.

### Perfiles

| Perfil | Uso |
|---|---|
| `dev` | Desarrollo local: muestra el SQL, logs en `DEBUG` y Swagger UI habilitado. |
| `prod` | Producción: no muestra SQL y Swagger UI queda bloqueado por la configuración de seguridad. |

## Ejecución

Clonar el repositorio:

```bash
git clone https://github.com/ThomasEfernandez/eventsAPIRESTful.git
cd eventsAPIRESTful
```

### Con Maven

Definí las variables de entorno (o cargalas desde tu IDE) y ejecutá:

```bash
./mvnw spring-boot:run
```

### Con Docker Compose

```bash
docker compose up --build
```

Docker Compose toma las variables del archivo `.env` y expone la API en el puerto `8080`.

### Solo con Docker

```bash
docker build -t api-eventos .
docker run -p 8080:8080 --env-file .env api-eventos
```

La imagen usa un build multi-stage: compila con `eclipse-temurin:21-jdk` y ejecuta con `eclipse-temurin:21-jre`.

## Despliegue en Render

El proyecto se puede desplegar como **Web Service** de Render usando el `Dockerfile` del repositorio:

1. Crear un Web Service y conectar el repositorio de GitHub.
2. Elegir **Docker** como entorno.
3. Cargar en **Environment** las variables `SPRING_PROFILES_ACTIVE=prod`, `NEON_DB_URL`, `NEON_DB_USER`, `NEON_DB_PASSWORD`, `JWT_SECRET` y `JWT_EXPIRATION`.
4. Desplegar. Cada push a la rama `main` dispara un nuevo deploy.

> En el plan gratuito, Render suspende el servicio tras un tiempo sin tráfico, por lo que la primera petición puede tardar unos segundos.

## Documentación de la API

Con la aplicación en ejecución en el perfil `dev`:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Especificación OpenAPI: http://localhost:8080/v3/api-docs

## Autenticación y roles

La API usa **JWT**. Las rutas bajo `/api/v1/auth/**` son públicas y el resto requiere un token válido. Enviá el token en el header de cada petición:

```
Authorization: Bearer <token>
```

| Rol | Permisos |
|---|---|
| `USER` | Consulta de eventos, categorías y oradores |
| `ADMIN` | Todo lo anterior, más crear, actualizar y eliminar |

Los tokens expiran según el valor de `JWT_EXPIRATION`.

## Endpoints

Todas las rutas tienen el prefijo `/api/v1`.

### Autenticación

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | `/auth/[COMPLETAR]` | Iniciar sesión. Cuerpo: `username` y `password` | Público |
| POST | `/auth/[COMPLETAR]` | Registrar un usuario. Cuerpo: `username`, `email` y `password` | Público |

### Eventos (`/events`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| GET | `/events` | Listar eventos con paginación. Parámetros: `name` (filtro), `page`, `size`, `sort`. Por defecto: página 0, tamaño 10, orden por nombre | ADMIN, USER |
| GET | `/events/{id}` | Obtener un evento por ID | ADMIN, USER |
| POST | `/events` | Crear un evento | ADMIN |
| PUT | `/events/{id}` | Actualizar un evento | ADMIN |
| DELETE | `/events/{id}` | Eliminar un evento | ADMIN |
| GET | `/events/problematic` | Listar eventos con carga ineficiente de detalles (problema N+1) | ADMIN, USER |
| GET | `/events/optimized-join-fetch` | Listar eventos optimizado con `JOIN FETCH` | ADMIN, USER |
| GET | `/events/optimized/all-details` | Listar eventos con todos sus detalles optimizados | ADMIN, USER |

### Categorías (`/categories`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| GET | `/categories` | Listar categorías | ADMIN, USER |
| GET | `/categories/{id}` | Obtener una categoría por ID | ADMIN, USER |
| POST | `/categories` | Crear una categoría | ADMIN |
| PUT | `/categories/{id}` | Actualizar una categoría | ADMIN |
| DELETE | `/categories/{id}` | Eliminar una categoría | ADMIN |

### Oradores (`/speakers`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| GET | `/speakers` | Listar oradores | ADMIN, USER |
| GET | `/speakers/{id}` | Obtener un orador por ID | ADMIN, USER |
| POST | `/speakers` | Crear un orador | ADMIN |
| PUT | `/speakers/{id}` | Actualizar un orador | ADMIN |
| DELETE | `/speakers/{id}` | Eliminar un orador | ADMIN |

## Modelo de datos

| Entidad | Tabla | Campos principales |
|---|---|---|
| `Event` | `events` | `id`, `name`, `description`, `date`, `location` |
| `Category` | `categories` | `id`, `name` (único), `description` |
| `Speaker` | `speakers` | `id`, `name`, `email` (único), `bio` |
| `User` | `users` | `id`, `name`, `username`, `email`, `password` |
| `Role` | `roles` | `id`, `name` |

Relaciones:

- **Evento ↔ Orador:** muchos a muchos (`event_speakers`).
- **Evento → Categoría:** muchos a uno.
- **Usuario ↔ Rol:** muchos a muchos (`users_roles`).
- **Usuario ↔ Evento (asistencia):** muchos a muchos (`user_attended_events`).

## Tests

```bash
./mvnw test
```

## Estructura del proyecto

```
src/main/java/com/gestion/eventos/api/
├── controller/      # Controladores REST
├── data/            # Carga de datos iniciales (DataLoader)
├── domain/          # Entidades JPA
├── dto/             # Objetos de transferencia de datos
├── mapper/          # Mapeadores MapStruct
├── repository/      # Repositorios Spring Data
├── service/         # Lógica de negocio
└── security/
    ├── config/      # Configuración de Spring Security y CORS
    ├── controller/  # Controlador de autenticación
    ├── dto/         # DTOs de login y registro
    ├── jwt/         # Filtro y utilidades JWT
    └── service/     # UserDetailsService
```

## Autor

**Thomas Ezequiel Fernandez**

- GitHub: [@ThomasEfernandez](https://github.com/ThomasEfernandez)

Curso: DevTalles en Udemy.
