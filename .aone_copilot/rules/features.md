# 主要功能模块

这是一个 **eFootball 球员管理系统**，提供球员数据的查询、管理功能，以及用户管理功能，前后端分离部署在阿里云 ECS 上。

## 核心功能

### 1. 球员管理（Player）

eFootball 游戏球员数据的 CRUD 管理，是项目的核心业务模块。

**相关代码：**
- Controller：`com.example.demo.controller.PlayerController`（路由前缀 `/api/player`）
- Service 接口：`com.example.demo.domain.service.PlayerService`
- Service 实现：`com.example.demo.service.impl.PlayerServiceImpl`
- 执行器：`com.example.demo.service.executor.PlayerCreateExe/PlayerQueryExe/PlayerUpdateExe`
- Repository：`com.example.demo.repository.PlayerRepository`
- Entity：`com.example.demo.domain.entity.PlayerEntity`

**API 接口：**

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/player` | 分页查询球员列表，支持多条件筛选 |
| GET | `/api/player/{id}` | 根据 ID 查询球员详情 |
| GET | `/api/player/enums` | 获取筛选项枚举（位置、联赛、球队、国家等） |
| POST | `/api/player` | 创建球员（需密码验证） |
| POST | `/api/player/{id}` | 更新球员信息（需密码验证） |
| POST | `/api/player/delete/{id}` | 删除球员（需密码验证） |

**球员属性：**`name`（姓名）、`position`（位置）、`status`（现役/历史）、`number`（号码）、`club`（球队）、`league`（联赛）、`country`（国家）、`height`（身高）、`foot`（惯用脚）、`cardImage`（卡面图片）

**查询能力：**
- 支持姓名模糊查询
- 支持位置、联赛、球队、国家、惯用脚精确筛选
- 支持身高范围筛选（`=`、`+` 大于、`-` 小于）
- 支持分页（`pageNum`、`pageSize`）

**写操作安全：** 创建、更新、删除操作均需传入密码，后端使用 MD5 校验。

**Redis 缓存策略：**
- `player`：单个球员详情缓存，按 ID 缓存，10 分钟 TTL
- `playerList`：分页列表缓存，按查询参数组合 hash 缓存，10 分钟 TTL
- `playerEnums`：枚举选项缓存，10 分钟 TTL
- 增删改操作自动清除相关缓存

---

### 2. 用户管理（User）

基础用户 CRUD 功能模块。

**相关代码：**
- Controller：`com.example.demo.controller.UserController`（路由前缀 `/api/users`）
- Service 接口：`com.example.demo.domain.service.UserService`
- Service 实现：`com.example.demo.service.impl.UserServiceImpl`
- 执行器：`com.example.demo.service.executor.UserCreateExe/UserQueryExe/UserUpdateExe`
- Repository：`com.example.demo.repository.UserRepository`
- Entity：`com.example.demo.domain.entity.UserEntity`

**API 接口：**

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/users` | 创建用户 |
| GET | `/api/users/{id}` | 根据 ID 查询用户 |
| PUT | `/api/users/{id}` | 更新用户信息 |
| DELETE | `/api/users/{id}` | 删除用户 |
| GET | `/api/users` | 分页查询用户列表 |

**Redis 缓存策略：**
- `user`：单个用户缓存，按 ID 缓存，10 分钟 TTL，查不到时不缓存（`unless = "#result.data == null"`）
- 更新、删除操作自动清除对应用户缓存

---

## 基础设施

### 数据存储

- **MySQL 8.0**：主数据库，使用 MyBatis Plus 操作，支持逻辑删除
- **Redis 7**：缓存层，使用 Spring Cache 注解（`@Cacheable`/`@CacheEvict`），JSON 序列化存储

### 配置管理

- **`RedisConfig`**：配置 Redis JSON 序列化（支持 `LocalDateTime`）和 `CacheManager`，开启 `@EnableCaching`
- **`CorsConfig`**：跨域配置，支持前端域名访问
- **`MybatisPlusConfig`**：分页插件配置

### 部署架构

```
用户浏览器
    ↓ HTTPS
Nginx（反向代理 + SSL）
    ↓ 内网
Spring Boot App（8080）
    ↓
MySQL（3306）+ Redis（6379）
```

所有服务通过 **Docker Compose** 编排，运行在阿里云 ECS（上海区域，`cn-shanghai`）。镜像托管在**阿里云容器镜像服务（ACR）**，通过 `docker buildx` 构建 `linux/amd64` 跨平台镜像。
