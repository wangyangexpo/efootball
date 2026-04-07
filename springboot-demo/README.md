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

### Local Development

1. Start MySQL with Docker:
   ```bash
   docker-compose up mysql
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   cd springboot-demo-start
   mvn spring-boot:run
   ```

4. Test API:
   ```bash
   curl http://localhost:8080/api/users
   ```

### Docker Deployment

1. Build and run all services:
   ```bash
   docker-compose up --build
   ```

2. Test API:
   ```bash
   curl http://localhost:8080/api/users
   ```

### Alibaba Cloud Deployment

1. Set environment variables:
   ```bash
   export SERVER_IP=your-server-ip
   ```

2. Run deployment script:
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user by ID |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| GET | /api/users | List users (paginated) |

## License

MIT