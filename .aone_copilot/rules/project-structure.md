# springboot-demo 项目结构

这是一个基于 **COLA（Clean Object-oriented Layered Architecture）** 分层架构的 Spring Boot 全栈演示项目，包含后端 Java 服务和前端 React 应用，部署在阿里云 ECS 上。

## 项目根目录

```
fullstack/
├── springboot-demo/        # 后端 Spring Boot 服务
├── frontend-efootball/     # 前端 React 应用
└── docs/                   # 项目文档
```

## 后端模块结构（COLA 六层架构）

```
springboot-demo/
├── springboot-demo-api/        # API 定义层：DTO、Request/Response 对象
├── springboot-demo-common/     # 公共层：工具类、常量、枚举
├── springboot-demo-domain/     # 领域层：Entity、Service 接口
├── springboot-demo-repository/ # 数据访问层：MyBatis Mapper、Repository
├── springboot-demo-service/    # 业务服务层：Service 实现、Executor
├── springboot-demo-start/      # 启动层：Controller、Config、Application 入口
├── sql/                        # 数据库初始化 SQL
├── docker-compose.yml          # 本地开发 Docker 编排
├── docker-compose.prod.yml     # 生产环境 Docker 编排（服务器上命名为 docker-compose.yml）
├── Dockerfile                  # 应用镜像构建文件
└── deploy.sh                   # 一键构建推送脚本
```

### 各模块关键目录

**springboot-demo-api**
- `com.example.demo.api/` — DTO 对象（`PlayerDTO`、`UserDTO`、`Response`）
- `com.example.demo.api.request/` — 请求对象（`PlayerQueryRequest`、`PlayerCreateRequest` 等）

**springboot-demo-common**
- `com.example.demo.common/` — `PageResult`、`ResultCode`、`Constants`

**springboot-demo-domain**
- `com.example.demo.domain.entity/` — 领域实体（`PlayerEntity`、`UserEntity`）
- `com.example.demo.domain.service/` — Service 接口（`PlayerService`、`UserService`）

**springboot-demo-repository**
- `com.example.demo.repository/` — Repository 封装类
- `com.example.demo.repository.mapper/` — MyBatis Plus Mapper 接口

**springboot-demo-service**
- `com.example.demo.service.impl/` — Service 实现类（`PlayerServiceImpl`、`UserServiceImpl`）
- `com.example.demo.service.executor/` — 命令执行器（`PlayerCreateExe`、`PlayerQueryExe`、`PlayerUpdateExe`）

**springboot-demo-start**
- `com.example.demo.controller/` — REST Controller（`PlayerController`、`UserController`）
- `com.example.demo.config/` — 配置类（`RedisConfig`、`CorsConfig`、`MybatisPlusConfig`）
- `com.example.demo.Application` — 启动入口

## 前端结构

```
frontend-efootball/
├── src/
│   ├── App.jsx         # 主应用组件
│   └── main.jsx        # 入口文件
├── index.html
├── vite.config.js      # Vite 构建配置
└── package.json
```

## 关键文件

- [springboot-demo/pom.xml](mdc:springboot-demo/pom.xml) — 父 POM，管理所有模块依赖版本
- [springboot-demo/springboot-demo-start/src/main/resources/application.yml](mdc:springboot-demo/springboot-demo-start/src/main/resources/application.yml) — 主配置文件
- [springboot-demo/springboot-demo-start/src/main/resources/application-dev.yml](mdc:springboot-demo/springboot-demo-start/src/main/resources/application-dev.yml) — 开发环境配置（MySQL、Redis 连接）
- [springboot-demo/springboot-demo-start/src/main/java/com/example/demo/config/RedisConfig.java](mdc:springboot-demo/springboot-demo-start/src/main/java/com/example/demo/config/RedisConfig.java) — Redis 缓存配置
- [springboot-demo/docker-compose.yml](mdc:springboot-demo/docker-compose.yml) — 本地开发容器编排
- [springboot-demo/deploy.sh](mdc:springboot-demo/deploy.sh) — 构建推送脚本
- [springboot-demo/README.md](mdc:springboot-demo/README.md) — 项目说明和部署文档

## 构建和运行

### 本地开发

```bash
# 在 springboot-demo/ 目录下执行
# 启动基础服务
docker compose up -d mysql redis

# 构建并启动应用
docker compose up --build -d app

# 查看日志
docker compose logs -f app
```

### 生产部署

```bash
# 构建跨平台镜像并推送到阿里云 ACR
docker buildx build --platform linux/amd64 \
  -t crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com/docker_wangyang/docker:latest \
  --push .

# 服务器上拉取并重启
docker compose pull app && docker compose up -d app
```
