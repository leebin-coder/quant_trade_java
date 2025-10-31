# Quant Trade Java

Quantitative Trading Platform based on Microservices Architecture

## Technology Stack

- Java 8
- Spring Boot 2.3.12
- Spring Cloud Hoxton.SR12
- Spring Cloud Alibaba 2.2.9
- Nacos 2.1.0 (Service Discovery & Config Center)
- PostgreSQL 12
- RabbitMQ 3
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
├── quant-message/             # Message module (RabbitMQ)
└── quant-storage/             # Storage module
```

### DDD Layers

Each business service follows DDD layering:

- **interfaces**: REST API layer
- **application**: Application service layer
- **domain**: Domain model layer (entities, value objects, repositories)
- **infrastructure**: Infrastructure layer (persistence implementation)

## Prerequisites

- JDK 8
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL 12+ (running on localhost:5432)

## Database Setup

This project uses an external PostgreSQL database. Connection details:

- **Host**: localhost:5432
- **Database**: quant_trade
- **User**: libin
- **Password**: libin122351

### Initialize Database

Create the database (if not exists):

```bash
./init-database.sh
```

Or manually:

```bash
psql -h localhost -p 5432 -U libin -c "CREATE DATABASE quant_trade;"
```

**Note**: Database tables will be automatically created by Flyway migration scripts when services start for the first time.

## Build

Build all modules:

```bash
cd quant-parent
mvn clean install
```

## Run with Docker Compose

Start all services (including infrastructure and business services):

```bash
docker-compose up -d
```

Stop all services:

```bash
docker-compose down
```

## Local Development

### 1. Initialize Database

```bash
./init-database.sh
```

This will create the `quant_trade` database if it doesn't exist.

### 2. Start Infrastructure Services

```bash
./start-infra.sh
```

This will start:
- RabbitMQ (Message Broker)
- Nacos (Service Registry & Config Center)

**Note**: PostgreSQL is not started in Docker as we use the external PostgreSQL instance on localhost:5432.

### 3. Build Project

```bash
cd quant-parent
mvn clean install -DskipTests
```

### 4. Run Services in IDE

Run the following main classes in your IDE:
- `com.quant.gateway.GatewayApplication` (Port 8080)
- `com.quant.user.UserApplication` (Port 8081)
- `com.quant.trade.TradeApplication` (Port 8082)
- `com.quant.risk.RiskApplication` (Port 8083)
- `com.quant.market.MarketApplication` (Port 8084)
- `com.quant.strategy.StrategyApplication` (Port 8085)

**On first startup**, Flyway will automatically create all database tables from migration scripts located in:
- `quant-user/src/main/resources/db/migration/`
- `quant-trade/src/main/resources/db/migration/`
- `quant-risk/src/main/resources/db/migration/`
- `quant-market/src/main/resources/db/migration/`
- `quant-strategy/src/main/resources/db/migration/`

### 5. Stop Infrastructure Services

```bash
./stop-infra.sh
```

## Services

### Infrastructure Services

- **PostgreSQL**: `localhost:5432` (External)
  - Database: `quant_trade`
  - Username: `libin`
  - Password: `libin122351`
  - Connection test: `psql -h localhost -p 5432 -U libin -d quant_trade`

- **RabbitMQ**: `localhost:5672`
  - Management UI: http://localhost:15672
  - Username: `guest`
  - Password: `guest`

- **Nacos**: http://localhost:8848/nacos
  - Username: `nacos`
  - Password: `nacos`

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

## Configuration

### Environment Variables

Environment variables are configured in `.env` file:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351

# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Nacos Configuration
NACOS_SERVER=localhost:8848
NACOS_NAMESPACE=public
```

### Nacos Configuration

Services automatically register with Nacos on startup. You can view and manage services in the Nacos console:

http://localhost:8848/nacos

Default credentials:
- Username: `nacos`
- Password: `nacos`

## Development

### Add New Service

1. Create a new module following the DDD structure
2. Add module to `quant-parent/pom.xml`
3. Add Nacos discovery and config dependencies
4. Configure `application.yml` with Nacos settings
5. Add routing rules in API Gateway
6. Add service to `docker-compose.yml`

### Database Migration

Use Flyway or Liquibase for database schema management (to be implemented).

## Troubleshooting

### Port Already in Use

If you encounter port conflicts, modify the port in each service's `application.yml`.

### Nacos Connection Failed

Ensure Nacos is running:
```bash
docker ps | grep quant-nacos
```

Check Nacos logs:
```bash
docker logs quant-nacos
```

### Service Not Registered

1. Check Nacos console: http://localhost:8848/nacos
2. Verify `NACOS_SERVER` environment variable
3. Check service logs for connection errors

## License

MIT License
