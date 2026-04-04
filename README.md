# WordCrush Server

WordCrush Server is the backend for the WordCrush app. It uses Spring Boot 3, MySQL, Redis, Flyway, JWT, and Docker.

## Stack

- Java 17
- Spring Boot 3
- Spring Web / Validation / Security
- Spring Data JPA
- MySQL 8
- Redis 7
- Flyway
- JWT
- Springdoc OpenAPI / Swagger UI
- Docker / Docker Compose
- Maven

## Profiles

This project uses explicit Spring profiles:

- `dev` is the default profile
- `prod` is used for server deployment

The active profile is controlled by `SPRING_PROFILES_ACTIVE`.

## Local Development

Run the app with the default `dev` profile:

```bash
./mvnw spring-boot:run
```

In `dev`, Swagger and Actuator details stay enabled, and Spring Boot Docker Compose integration is on.

## Production Deployment

Run the Docker stack with the production profile:

```bash
docker compose up --build -d
```

The compose file sets `SPRING_PROFILES_ACTIVE=prod`, so the app starts with production settings automatically.

Production behavior:

- Swagger is disabled
- Spring Boot Docker Compose integration is disabled
- Actuator only exposes `health`
- JVM memory is capped
- MySQL and Redis container memory are capped

## Key Environment Variables

- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `REDIS_DATABASE`
- `JWT_SECRET`
- `JWT_EXPIRATION_HOURS`
- `BOOTSTRAP_ADMIN_USERNAME`
- `BOOTSTRAP_ADMIN_PASSWORD`

## API Notes

- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/user/checkToken`
- `POST /api/getTopNRecord`
- `GET /api/user/avatar/{username}`
- `/swagger-ui/**`
- `/api-docs/**`
- `/actuator/**`

## Useful Links

- [docs/database-design.md](./docs/database-design.md)
- [src/main/resources/db/migration/V1__init_schema.sql](./src/main/resources/db/migration/V1__init_schema.sql)
