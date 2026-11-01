# API de Gestión de Eventos

API REST para la gestión de eventos, construida con **Spring Boot 4** y **Java 21**. Incluye autenticación mediante **JWT**, persistencia en **PostgreSQL**, documentación interactiva con **Swagger/OpenAPI** y despliegue mediante **Docker**.

## Contexto

Proyecto desarrollado con fines educativos, como trabajo práctico de un curso de [DevTalles](https://devtalles.com) en Udemy.

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.1 (Spring Web MVC) |
| Persistencia | Spring Data JPA, PostgreSQL (Neon), H2 |
| Seguridad | Spring Security, JWT (jjwt 0.13.0) |
| Validación | Spring Boot Validation |
| Mapeo de objetos | MapStruct 1.6.3, Lombok |
| Documentación | springdoc-openapi 2.8.13 (Swagger UI) |
| Monitoreo | Spring Boot Actuator |
| Build | Maven (Maven Wrapper incluido) |
| Contenedores | Docker, Docker Compose |

## Requisitos previos

- JDK 21
- Docker y Docker Compose (opcional, para correr con contenedores)
- Una base de datos PostgreSQL (el proyecto está configurado para [Neon](https://neon.tech))

No hace falta instalar Maven: se usa el wrapper (`./mvnw`).

## Configuración

La aplicación se configura mediante variables de entorno. Creá un archivo `.env` en la raíz del proyecto tomando como base este ejemplo:

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

> ⚠️ **Nunca subas el archivo `.env` al repositorio.** Está incluido en `.gitignore`; usá `.env.example` con valores de ejemplo.

## Ejecución

Clonar el repositorio:

```bash
git clone https://github.com/ThomasEfernandez/eventsAPIRESTful.git
cd eventsAPIRESTful
```

### Con Maven

Definí las variables de entorno y levantá la aplicación:

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

## Documentación de la API

Con la aplicación en ejecución:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Especificación OpenAPI: http://localhost:8080/v3/api-docs
- Health check: http://localhost:8080/actuator/health

## Autenticación

La API usa **JWT**. Para acceder a los endpoints protegidos:

1. Obtené un token autenticándote con tus credenciales.
2. Enviá el token en el header de cada petición:

```
Authorization: Bearer <token>
```

Los tokens expiran según el valor de `JWT_EXPIRATION`.

## Endpoints principales

> Completá esta tabla con los endpoints reales de tu API. El detalle completo está siempre disponible en Swagger UI.

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/login` | [COMPLETAR] Iniciar sesión | No |
| POST | `/auth/register` | [COMPLETAR] Registrar usuario | No |
| GET | `/eventos` | [COMPLETAR] Listar eventos | Sí |
| GET | `/eventos/{id}` | [COMPLETAR] Obtener un evento | Sí |
| POST | `/eventos` | [COMPLETAR] Crear un evento | Sí |
| PUT | `/eventos/{id}` | [COMPLETAR] Actualizar un evento | Sí |
| DELETE | `/eventos/{id}` | [COMPLETAR] Eliminar un evento | Sí |

## Tests

```bash
./mvnw test
```

## Estructura del proyecto

```
.
├── src/
│   ├── main/
│   │   ├── java/com/gestion/eventos/api/
│   │   └── resources/
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

> Ajustá el árbol según la organización real de tus paquetes (controllers, services, repositories, dto, mappers, security, etc.).

## Autor

**Thomas Ezequiel Fernandez**

- GitHub: [@ThomasEfernandez](https://github.com/ThomasEfernandez)

Curso: DevTalles en Udemy.
