# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a full-stack efootball player management system with:
- **Backend**: Spring Boot (Java 11) with COLA layered architecture
- **Frontend**: React + Vite + Ant Design on port 3000

## Backend Architecture (COLA)

The Spring Boot backend follows COLA (Clean Object-oriented Layered Architecture) with 6 modules:

```
springboot-demo/
├── springboot-demo-api       # API definitions (DTOs, Request/Response objects)
├── springboot-demo-common    # Common utilities and constants
├── springboot-demo-domain    # Domain models and service interfaces
├── springboot-demo-repository # Data access layer (MyBatis Plus mappers/entities)
├── springboot-demo-service   # Business service implementations
└── springboot-demo-start     # Application entry point, controllers, configs
```

Module dependency order: api → common → domain → repository → service → start

Controllers live in `springboot-demo-start`. Services implement interfaces from `springboot-demo-domain`.

## Key Commands

### Backend (in `springboot-demo/` directory)

```bash
# Start infrastructure (MySQL + Redis)
docker compose up -d mysql redis

# Rebuild and restart app container (after code changes)
docker compose up --build -d app

# View app logs
docker compose logs -f app

# Reset database (clears all data)
docker compose down -v && docker compose up -d mysql redis

# Re-import database data
docker exec -i demo-mysql mysql -uroot -proot springboot_demo < sql/init.sql

# Build for production
mvn clean package -DskipTests

# Build multi-platform image and push to ACR
docker buildx build --platform linux/amd64 \
  -t crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com/docker_wangyang/docker:latest \
  --push .
```

### Frontend (in `frontend-efootball/` directory)

```bash
npm run dev    # Start dev server on port 3000 (proxies /api to localhost:8080)
npm run build  # Build production bundle to dist/
```

### Deployment

```bash
# One-click deploy (build + push + server update)
./springboot-demo/deploy.sh
```

## Environment Variables

Backend requires these env vars (see `springboot-demo/.env.example`):

- `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `DB_USER`, `DB_PASS` - MySQL config
- `REDIS_HOST`, `REDIS_PORT` - Redis config
- `OSS_ENDPOINT`, `OSS_ACCESS_KEY_ID`, `OSS_ACCESS_KEY_SECRET`, `OSS_BUCKET_NAME`, `OSS_URL_PREFIX` - Aliyun OSS config

Create `.env` from `.env.example` before running docker compose.

## Tech Stack Details

Backend: Spring Boot 2.7.18, MyBatis Plus 3.5.3.1, MySQL 8.0, Redis, Lombok, MapStruct, Aliyun OSS

Frontend: React 18, Vite 5, Ant Design 5, Axios, React Router 7

## API Proxy

Frontend dev server proxies `/api/*` requests to `http://localhost:8080`.

## Database

Schema initialized via `springboot-demo/sql/init.sql`. MyBatis Plus XML mappers in `springboot-demo-repository/src/main/resources/mappers/`.