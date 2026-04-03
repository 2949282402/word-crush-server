# WordCrush 数据库与缓存设计

## 设计目标

- 满足 Android 客户端当前全部后端接口
- 覆盖可写进简历的工程能力：结构化建模、缓存化登录态、容器化部署、迁移脚本
- 保持接口兼容的同时，提升后端可维护性

## MySQL 表设计

### `users`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `BIGINT` | 主键，自增 |
| `username` | `VARCHAR(32)` | 用户名，唯一索引 |
| `password_hash` | `VARCHAR(100)` | BCrypt 密码哈希 |
| `status` | `TINYINT` | 账号状态 |
| `created_at` | `DATETIME(3)` | 创建时间 |
| `updated_at` | `DATETIME(3)` | 更新时间 |

索引：

- `uk_users_username (username)`

### `game_records`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `BIGINT` | 主键，自增 |
| `user_id` | `BIGINT` | 用户外键 |
| `game_type` | `TINYINT` | 游戏类型，`0=Classic`，`1=Timed` |
| `score` | `INT` | 单局得分 |
| `played_at` | `DATETIME(3)` | 客户端上传的局时间 |
| `created_at` | `DATETIME(3)` | 创建时间 |
| `updated_at` | `DATETIME(3)` | 更新时间 |

索引与约束：

- `fk_game_records_user`
- `uk_game_records_record (user_id, game_type, score, played_at)`
- `idx_game_records_user_time (user_id, played_at DESC)`
- `idx_game_records_type_score (game_type, score DESC, played_at ASC)`

### `game_record_words`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `BIGINT` | 主键，自增 |
| `game_record_id` | `BIGINT` | 战绩外键 |
| `sort_order` | `INT` | learned words 顺序 |
| `word_content` | `VARCHAR(255)` | 单词摘要内容 |
| `created_at` | `DATETIME(3)` | 创建时间 |
| `updated_at` | `DATETIME(3)` | 更新时间 |

索引与约束：

- `fk_game_record_words_record`
- `idx_game_record_words_record (game_record_id, sort_order)`

## Redis 设计

### Key 约定

- `wordcrush:auth:token:{token}`
  - 值：token session JSON
  - 作用：快速校验登录态
  - TTL：默认 168 小时

- `wordcrush:auth:user:{userId}`
  - 值：用户持有 token 集合
  - 作用：修改密码后批量失效会话

## 设计说明

- MySQL 存放强一致业务数据
- Redis 承担会话缓存和 token 索引
- Flyway 负责数据库版本演进
- Docker Compose 负责完整部署交付
