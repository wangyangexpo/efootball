---
name: springboot-demo-design
description: Spring Boot demo project design for deployment to Alibaba Cloud
type: project
---

# Spring Boot Demo Project Design

## Overview

Create a minimal Spring Boot demo project based on alsc-laboon-base architecture, removing all Alibaba internal dependencies. The project demonstrates a complete backend service from local development to deployment on Alibaba Cloud server.

## Why

- User wants to learn enterprise-level Java project architecture (COLA layered)
- Need a clean, runnable project without Alibaba internal dependencies
- Demonstrate complete deployment pipeline to Alibaba Cloud

## How to apply

Follow this design to create a multi-module Spring Boot project with CRUD API, MySQL integration, and Docker deployment configuration.

---

## Module Structure

```
springboot-demo/
├── springboot-demo-api/          # API definitions (DTOs, Request/Response objects)
├── springboot-demo-common/       # Common utilities (Constants, Enums, Helpers)
├── springboot-demo-domain/       # Domain models (Entities, Domain services)
├── springboot-demo-repository/   # Data access (Mappers, Repository interfaces)
├── springboot-demo-service/      # Business logic (Service implementations, Executors)
├── springboot-demo-start/        # Startup module (Application, Controllers, Configs)
├── pom.xml                       # Parent POM
├── Dockerfile                    # Docker build file
├── docker-compose.yml            # Local Docker orchestration
├── deploy.sh                     # Alibaba Cloud deployment script
└── README.md                     # Project documentation
```

---

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 11 | Runtime |
| Spring Boot | 2.7.18 | Web framework |
| MyBatis Plus | 3.5.3.1 | ORM framework |
| MySQL | 8.0 | Database |
| Lombok | 1.18.22 | Code generation |
| MapStruct | 1.5.5.Final | Object mapping |

**Removed Alibaba Dependencies**:
- Pandora Boot -> Standard Spring Boot
- HSF RPC -> REST Controller
- Diamond -> application.yml
- Tair -> None (not needed for demo)
- Eagleeye -> None (not needed for demo)

---

## Demo Business: User Management CRUD

### Database Schema

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    email VARCHAR(100) COMMENT 'Email address',
    status INT DEFAULT 1 COMMENT 'Status: 1-active, 0-inactive',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';
```

### REST API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user by ID |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| GET | /api/users | List users (paginated) |

### Request/Response Models

**Create Request**:
```json
{
  "username": "testuser",
  "email": "test@example.com"
}
```

**Response Structure**:
```json
{
  "code": "200",
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "status": 1,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  }
}
```

---

## Layer Design

### API Layer (springboot-demo-api)

Contains all public API contracts:
- `Response<T>` - Generic response wrapper
- `UserDTO` - User data transfer object
- `UserCreateRequest` - Create request
- `UserUpdateRequest` - Update request
- `UserQueryRequest` - Query request with pagination

### Common Layer (springboot-demo-common)

Shared utilities:
- `Constants` - Common constants
- `ResultCode` - Response code enumeration
- `PageResult<T>` - Pagination result wrapper

### Domain Layer (springboot-demo-domain)

Domain models:
- `UserEntity` - User domain entity
- `UserService` - User domain service interface

### Repository Layer (springboot-demo-repository)

Data access:
- `UserMapper` - MyBatis mapper interface
- `UserMapper.xml` - SQL mapping file
- `UserRepository` - Repository abstraction

### Service Layer (springboot-demo-service)

Business implementation:
- `UserServiceImpl` - Service implementation
- `UserCreateExe` - Create command executor
- `UserQueryExe` - Query executor

### Start Layer (springboot-demo-start)

Application entry:
- `Application.java` - Spring Boot main class
- `UserController` - REST controller
- `application.yml` - Configuration

---

## Configuration

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: springboot-demo
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:springboot_demo}
    username: ${DB_USER:root}
    password: ${DB_PASS:root}
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath*:mappers/**/*.xml
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.example.demo.repository.mapper: debug
```

---

## Docker Configuration

### Dockerfile

```dockerfile
FROM openjdk:11-jre-slim
LABEL maintainer="demo@example.com"
WORKDIR /app
COPY springboot-demo-start/target/springboot-demo-start.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: demo-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: springboot_demo
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql

  app:
    build: .
    container_name: demo-app
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_NAME: springboot_demo
      DB_USER: root
      DB_PASS: root
    depends_on:
      - mysql

volumes:
  mysql_data:
```

---

## Deployment Script

### deploy.sh

```bash
#!/bin/bash
# Deployment script for Alibaba Cloud

APP_NAME="springboot-demo"
IMAGE_NAME="registry.cn-hangzhou.aliyuncs.com/your-namespace/${APP_NAME}"
SERVER_IP="your-server-ip"

# Build
mvn clean package -DskipTests

# Docker build & push
docker build -t ${IMAGE_NAME}:latest .
docker push ${IMAGE_NAME}:latest

# Deploy to server
ssh root@${SERVER_IP} "docker pull ${IMAGE_NAME}:latest && docker stop ${APP_NAME} || true && docker rm ${APP_NAME} || true && docker run -d --name ${APP_NAME} -p 8080:8080 ${IMAGE_NAME}:latest"

echo "Deployed successfully!"
```

---

## Local Development Workflow

1. Start MySQL: `docker-compose up mysql`
2. Run init SQL: Located in `sql/init.sql`
3. Build project: `mvn clean install`
4. Run Application: Execute `springboot-demo-start/Application.java`
5. Test API: `curl http://localhost:8080/api/users`

---

## Success Criteria

- Project builds successfully with `mvn clean install`
- Application starts locally and connects to MySQL
- All CRUD APIs return correct JSON responses
- Docker image builds and runs successfully
- Deployment script works for Alibaba Cloud ECS