# Quant Trade Java

Quantitative Trading Platform based on Microservices Architecture

## Technology Stack

- Java 21
- Spring Boot 3.x
- Spring Cloud
- Spring Cloud Alibaba
- Nacos (Service Discovery & Config Center)
- PostgreSQL
- Maven 3.9
- Docker

## Architecture

This project follows Domain-Driven Design (DDD) principles with a microservices architecture.

### Project Structure

```
quant-trade-java/
├── quant-parent/              # Parent POM
├── quant-common/              # Common utilities and base classes
├── quant-gateway/             # API Gateway (Port: 8080)
├── quant-user/                # User Service (Port: 8081)
├── quant-trade/               # Trade Service (Port: 8082)
├── quant-risk/                # Risk Management Service (Port: 8083)
├── quant-market/              # Market Data Service (Port: 8084)
├── quant-strategy/            # Strategy Service (Port: 8085)
├── quant-database/            # Database module (JPA)
├── quant-cache/               # Cache module
├── quant-message/             # Message module
└── quant-storage/             # Storage module
```

### DDD Layers

Each business service follows DDD layering:

- **interfaces**: REST API layer
- **application**: Application service layer
- **domain**: Domain model layer (entities, value objects, repositories)
- **infrastructure**: Infrastructure layer (persistence implementation)

## Prerequisites

- Docker and Docker Compose
- PostgreSQL (external, not in Docker)

**Note:** No need to install Java or Maven locally - everything builds in Docker!

## Quick Start

### 1. Initialize PostgreSQL Database

```bash
# Connect to your PostgreSQL server
psql -h <your-postgres-host> -U libin -f docker/init-db.sql
```

This creates two databases:
- `quant_trade` - Application data
- `nacos_config` - Nacos configuration

### 2. Configure Environment

```bash
cp .env.example .env
vim .env  # Edit database connection settings
```

Update `.env` with your PostgreSQL configuration:
```env
DB_HOST=192.168.1.100    # Your PostgreSQL server IP
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351
NACOS_DB_NAME=nacos_config
```

### 3. Start Services

```bash
docker-compose up -d --build
```

### 4. Verify

```bash
# Check Nacos Console (访问控制台)
curl http://localhost:8888/nacos/
# Browser: http://localhost:8888/nacos (nacos/nacos)

# Check Nacos Server (检查服务端)
curl http://localhost:8848/nacos/v1/console/health/readiness

# Check Gateway
curl http://localhost:8080/actuator/health
```

For detailed deployment instructions, see [DEPLOYMENT.md](./DEPLOYMENT.md)

## Services

### Infrastructure Services

- **PostgreSQL**: External (not in Docker)
  - Application Database: `quant_trade`
  - Nacos Database: `nacos_config`
  - Configure in `.env` file

- **Nacos**:
  - Console UI: http://localhost:8888/nacos (控制台端口)
  - Server Port: 8848 (服务端口 - gRPC)
  - Username: `nacos`
  - Password: `nacos`
  - Version: 2.3.2 (鉴权已启用, ARM64 原生)
  - Data stored in PostgreSQL (persistent)
  - Resources: 2G memory limit, 1G reserved

### Business Services

- **API Gateway**: `http://localhost:8080`
- **User Service**: `http://localhost:8081`
- **Trade Service**: `http://localhost:8082`
- **Risk Service**: `http://localhost:8083`
- **Market Service**: `http://localhost:8084`
- **Strategy Service**: `http://localhost:8085`

## API Examples

### Health Check

```bash
# Check user service health via gateway
curl http://localhost:8080/api/user/actuator/health

# Check user service directly
curl http://localhost:8081/actuator/health
```

### User API

```bash
# Get user by ID
curl http://localhost:8080/api/user/user/1

# Create user
curl -X POST http://localhost:8080/api/user/user \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com"}'
```

## Docker

Each service has its own Dockerfile with multi-stage build:
- Build stage: Compile Java code with Maven
- Runtime stage: Run with JRE (Alpine based)

Services are configured in `docker-compose.yml` with:
- Health checks
- Auto-restart policies
- Network isolation
- Volume persistence

## Development

### Local Development (without Docker)

1. Start PostgreSQL and Nacos locally
2. Build the project:
   ```bash
   cd quant-parent
   mvn clean install -DskipTests
   ```
3. Run services in your IDE

### Add New Service

1. Create a new module following the DDD structure
2. Add module to `quant-parent/pom.xml`
3. Add Nacos discovery and config dependencies
4. Configure `application.yml` with Nacos settings
5. Add routing rules in API Gateway
6. Create Dockerfile for the service
7. Add service to `docker-compose.yml`

## Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
lsof -i :8080

# Stop the service using docker-compose
docker-compose down
```

### View Service Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f gateway
docker-compose logs -f user-service
```

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# View PostgreSQL logs
docker-compose logs postgres

# Connect to PostgreSQL container
docker exec -it quant-postgres psql -U postgres -d quant_trade
```

### Service Not Registered in Nacos

1. Check Nacos console: http://localhost:8848/nacos
2. Check service logs: `docker-compose logs <service-name>`
3. Verify network connectivity between containers

### Clean Restart

```bash
# Stop and remove all containers and volumes
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build
```

## License

MIT License
