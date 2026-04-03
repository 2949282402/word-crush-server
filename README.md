# WordCrush Server

WordCrush Server 是 `word-crush-app` 的后端服务，基于 Spring Boot 3 构建，提供账号、头像、排行榜、游戏记录等接口，并使用 MySQL、Redis、Flyway、JWT 组织完整的工程化能力。

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

## 当前认证模型

- 服务端使用 `JWT + Redis Session` 进行认证。
- 登录成功后会签发 token，并把会话写入 Redis。
- 同一账号采用单设备登录：新设备登录会撤销该用户历史 token。
- 改密后会立即撤销该用户全部 token。
- 受保护接口要求携带 token；旧客户端兼容以下三种传法：
  - `Authorization: Bearer <token>`
  - 请求头 `token: <token>`
  - 部分兼容接口仍可通过 `token` query 参数传入

## 鉴权白名单

以下路径无需登录：

- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/user/checkToken`
- `POST /api/getTopNRecord`
- `GET /api/user/avatar/{username}`
- `/swagger-ui/**`
- `/api-docs/**`
- `/actuator/**`

除以上白名单外，其余 `/api/**` 接口都需要通过 token 验证。

另外，以下接口除了要求 token 有效，还会校验“请求里的用户名必须与当前 token 对应用户一致”，防止跨账号操作：

- `POST /api/user/changePassword`
- `POST /api/user/avatar`
- `POST /api/addGameRecord`
- `POST /api/deleteGameRecord`
- `POST /api/getAllGameRecord`

## 主要能力

- 用户登录、注册、改密、token 校验
- 用户头像上传与读取
- 游戏记录新增、删除、查询
- 排行榜查询
- Redis 会话管理与 token 失效控制
- Flyway 数据库版本管理

## 接口约定

账号接口返回：

- `code / msg / data`

旧版游戏记录与排行榜接口返回：

- `status / message`

这部分是为了兼容 Android 客户端的既有协议。

## 目录结构

```text
src/main/java/com/wordcrush/server
├── common
├── config
├── module
├── security
└── WordCrushServerApplication.java
```

## 核心接口

账号接口：

- `POST /api/user/login`
- `GET /api/user/checkToken`
- `POST /api/user/register`
- `POST /api/user/changePassword`
- `POST /api/user/avatar`
- `GET /api/user/avatar/{username}`

游戏与排行榜接口：

- `POST /api/getTopNRecord`
- `POST /api/addGameRecord`
- `POST /api/deleteGameRecord`
- `POST /api/getAllGameRecord`

## 数据设计

- [docs/database-design.md](./docs/database-design.md)
- [src/main/resources/db/migration/V1__init_schema.sql](./src/main/resources/db/migration/V1__init_schema.sql)

Redis 中与认证相关的 key 约定：

- `wordcrush:auth:token:{token}`：token 对应的会话数据
- `wordcrush:auth:user:{userId}`：用户当前持有的 token 集合

## 本地运行

准备好 MySQL 和 Redis 后执行：

```bash
./mvnw spring-boot:run
```

默认地址：

- App: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

启动后会自动确保存在默认管理员账号：

- 用户名：`admin`
- 密码：`123456`

## Docker Compose

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
- `REDIS_PASSWORD`
- `REDIS_DATABASE`
- `JWT_SECRET`
- `JWT_EXPIRATION_HOURS`
- `BOOTSTRAP_ADMIN_USERNAME`
- `BOOTSTRAP_ADMIN_PASSWORD`

## 联调建议

- Android 端登录后应保存服务端返回的 `data.token`
- 后续受保护请求统一带 token
- 如果接口返回 `401`，客户端应清理本地会话并回到登录页
