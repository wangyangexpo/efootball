# springboot-demo 代码风格规范

## 整体架构约定

本项目严格遵循 **COLA 分层架构**，各层职责明确，禁止跨层调用：

```
Controller → Service 接口 → Service 实现 → Executor → Repository → Mapper
```

- **Controller** 只负责接收请求、调用 Service、返回响应，不含业务逻辑
- **Service 实现** 负责密码校验、缓存注解、结果封装，将具体业务委托给 Executor
- **Executor** 负责具体业务逻辑（查询、创建、更新），每个操作对应一个独立的 Executor 类
- **Repository** 封装 Mapper，提供语义化的数据访问方法，不直接在 Service 中使用 Mapper
- **Entity** 只在 domain 和 repository 层流转，不暴露到 Controller 层

## Java 代码风格

### 命名约定

- **类名**：大驼峰，语义清晰
  - Controller：`PlayerController`、`UserController`
  - Service 接口：`PlayerService`、`UserService`（定义在 domain 层）
  - Service 实现：`PlayerServiceImpl`、`UserServiceImpl`
  - Executor：`PlayerCreateExe`、`PlayerQueryExe`、`PlayerUpdateExe`（操作类型 + `Exe` 后缀）
  - Repository：`PlayerRepository`、`UserRepository`
  - Entity：`PlayerEntity`、`UserEntity`（`Entity` 后缀）
  - DTO：`PlayerDTO`、`UserDTO`（`DTO` 后缀）
  - Request：`PlayerCreateRequest`、`PlayerQueryRequest`（操作类型 + `Request` 后缀）

- **方法名**：小驼峰动词短语
  - 查询：`getById`、`list`、`getEnums`
  - 写操作：`create`、`update`、`delete`、`execute`

- **常量**：全大写下划线，定义在 `ResultCode` 枚举或 `Constants` 类中

### 统一响应格式

所有 API 接口必须返回 `Response<T>` 包装类：

```java
// 成功
return Response.success(data);
return Response.success();  // 无数据

// 失败（使用 ResultCode 枚举）
return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), ResultCode.PLAYER_NOT_FOUND.getMessage());
```

错误码统一在 `ResultCode` 枚举中定义，格式：
- 通用错误：`200`、`400`、`404`、`500`
- User 业务：`1001`、`1002`...
- Player 业务：`2001`、`2002`...
- 新业务模块从下一个千位开始

### 分页查询

分页结果统一使用 `PageResult<T>` 包装：

```java
PageResult.of(list, total, pageNum, pageSize);
```

### 注解使用

- Entity 类使用 `@TableName`、`@TableId`、`@TableLogic` 等 MyBatis Plus 注解
- Entity 和 DTO 必须实现 `Serializable`（Redis 缓存需要）
- Controller 参数校验使用 `@Valid` + JSR-303 注解
- GET 请求查询参数使用 `@ModelAttribute`，POST 请求体使用 `@RequestBody`

### Redis 缓存规范

- 缓存注解加在 **Service 实现类**方法上，不加在 Executor 上
- 查询方法使用 `@Cacheable`，写操作使用 `@CacheEvict`
- 多个缓存同时清除使用 `@Caching`
- 缓存 key 命名：`value` 为缓存空间名（如 `player`、`playerList`），`key` 用 SpEL 表达式

```java
@Cacheable(value = "player", key = "#id")
@CacheEvict(value = "player", key = "#id")
@Cacheable(value = "playerList", key = "T(java.util.Objects).hash(#request.pageNum, ...)")
```

## 注释规范

- 类级别注释简洁说明职责，如 `/** Player query executor */`
- 复杂业务逻辑、缓存策略、密码校验等需要注释说明
- 简单的 getter/setter、标准 CRUD 方法无需注释

## 异常处理

- Service 层捕获 `RuntimeException`，转换为 `Response.fail()` 返回，不向上抛出
- 不存在的资源返回对应的 `ResultCode`（如 `PLAYER_NOT_FOUND`），不抛 404 异常
- 密码校验失败返回 `PLAYER_PASSWORD_INVALID`，不抛异常

## 依赖注入

统一使用 `@Autowired` 字段注入（项目现有风格），不使用构造器注入。

## 逻辑删除

使用 MyBatis Plus 逻辑删除，`deleted` 字段：`0` 未删除，`1` 已删除，在全局配置中统一设置，Entity 字段加 `@TableLogic`。
