# Spring Boot Demo Project

A minimal Spring Boot demo project demonstrating COLA layered architecture and deployment to Alibaba Cloud.

## Architecture

This project follows COLA (Clean Object-oriented Layered Architecture) with 6 modules:

- **springboot-demo-api**: API definitions (DTOs, Request/Response)
- **springboot-demo-common**: Common utilities and constants
- **springboot-demo-domain**: Domain models and service interfaces
- **springboot-demo-repository**: Data access layer (MyBatis)
- **springboot-demo-service**: Business service implementations
- **springboot-demo-start**: Application entry point

## Tech Stack

- Java 11
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- Lombok
- MapStruct

## Quick Start

### Prerequisites

- JDK 11+
- Maven 3.6+
- Docker & Docker Compose

---

## 本地开发调试

> 以下命令需在 `springboot-demo/` 目录下执行

### 启动基础服务（MySQL + Redis）

```bash
docker compose up -d mysql redis
```

### 重新导入数据库数据

```bash
docker exec -i demo-mysql mysql -uroot -proot springboot_demo < sql/init.sql
```

### 重新构建并启动 app（改了后端代码后执行）

```bash
docker compose up --build -d app
```

### 查看 app 启动日志

```bash
docker compose logs -f app
```

### 停止所有服务

```bash
docker compose down
```

### 重置数据库（清空 volume 重新初始化，会丢失数据）

```bash
docker compose down -v
docker compose up -d mysql redis
```

---

## 打包推送到阿里云 Docker 镜像仓库（ACR）

### 构建跨平台镜像并推送（本地 Mac → 服务器 linux/amd64）

```bash
# 登录阿里云 ACR
docker login crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com

# 构建并推送镜像（在 springboot-demo 目录下执行）
docker buildx build \
  --platform linux/amd64 \
  -t crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com/docker_wangyang/docker:latest \
  --push \
  .
```

---

## 阿里云服务器重新部署

### 首次部署：拷贝配置文件到服务器

```bash
# 创建目录
ssh wangyangexpo@139.224.252.176 "mkdir -p /home/wangyangexpo/springboot-demo/sql"

# 拷贝 docker-compose 生产配置
scp /Users/alsc/Documents/shared/fullstack/springboot-demo/docker-compose.prod.yml wangyangexpo@139.224.252.176:/home/wangyangexpo/springboot-demo/docker-compose.yml
```

### 更新 SQL 文件到服务器（改了 init.sql 后执行）

```bash
scp /Users/alsc/Documents/shared/fullstack/springboot-demo/sql/init.sql wangyangexpo@139.224.252.176:/home/wangyangexpo/springboot-demo/sql/init.sql
```
### 在服务器上拉取最新镜像并重启

```bash
# 重新导入数据库
docker exec -i demo-mysql mysql -uroot -proot springboot_demo < sql/init.sql

# 拉取最新镜像
docker compose pull app

# 重启 app 容器
docker compose up -d app
```

### 查看服务器上的 app 日志

```bash
sudo docker compose -f /home/wangyangexpo/springboot-demo/docker-compose.yml logs -f app
```

### 一键部署（本地执行，自动构建推送并通知服务器更新）

```bash
chmod +x deploy.sh
./deploy.sh
```

---

## API Endpoints

| Method | Path            | Description            |
| ------ | --------------- | ---------------------- |
| POST   | /api/users      | Create user            |
| GET    | /api/users/{id} | Get user by ID         |
| PUT    | /api/users/{id} | Update user            |
| DELETE | /api/users/{id} | Delete user            |
| GET    | /api/users      | List users (paginated) |

## License

MIT
