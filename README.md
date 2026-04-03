# WordCrush Server

WordCrush Server 是面向 `word-crush-app` Android 客户端重写的新后端，基于标准 Spring Boot 分层架构实现，兼容现有客户端接口契约，并补齐了 MySQL、Redis、Flyway、JWT、Swagger、Docker 等工程能力。

## 技术栈

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

## 项目亮点

- 严格兼容 Android 端既有接口，包括账号接口与旧版 `status/message` 游戏记录接口
- 使用 MySQL 持久化用户、战绩、战绩单词明细，支持结构化表设计和索引优化
- 使用 Redis 存储登录态与 token 索引，支持 token 校验和改密后的会话失效
- 通过 Flyway 管理数据库版本，适合持续交付和团队协作
- 提供 Swagger 文档与 Actuator 健康检查，方便联调和部署
- 通过 Dockerfile + Docker Compose 一键拉起应用、MySQL、Redis

## 目录结构

```text
src/main/java/com/wordcrush/server
├── common
├── config
├── controller
├── domain
├── dto
├── security
├── service
└── support
```

## 核心接口

账号接口：

- `POST /api/user/login`
- `GET /api/user/checkToken`
- `POST /api/user/register`
- `POST /api/user/changePassword`

游戏记录与排行榜接口：

- `POST /api/getTopNRecord`
- `POST /api/addGameRecord`
- `POST /api/deleteGameRecord`
- `POST /api/getAllGameRecord`

说明：

- 账号接口返回 `code/msg/data`
- 游戏记录接口保持 Android 客户端当前依赖的 `status/message` 结构

## 数据库设计

- [docs/database-design.md](./docs/database-design.md)
- [src/main/resources/db/migration/V1__init_schema.sql](./src/main/resources/db/migration/V1__init_schema.sql)

## 运行方式

### 本地开发

准备好 MySQL 和 Redis 后执行：

```bash
./mvnw spring-boot:run
```

默认地址：

- App: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

启动后会自动确保存在管理员账号：

- `admin`
- `123456`

### Docker Compose

```bash
docker compose up --build -d
```

会拉起：

- `word-crush-server`
- `word-crush-mysql`
- `word-crush-redis`

## 关键环境变量

- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`
- `JWT_EXPIRATION_HOURS`
- `BOOTSTRAP_ADMIN_USERNAME`
- `BOOTSTRAP_ADMIN_PASSWORD`
