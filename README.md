# Jackpot Game Service

A Spring Boot REST API service for managing jackpots and player bets with PostgreSQL database integration.

## Features

- ✅ Create jackpots with configurable win probabilities
- ✅ Place bets on jackpots with automatic win/loss determination
- ✅ Retrieve jackpot and betting history
- ✅ PostgreSQL database integration
- ✅ RESTful API with OpenAPI/Swagger documentation
- ✅ Comprehensive unit and integration tests (41+ tests)
- ✅ Docker containerization with single-command deployment
- ✅ **Spring Boot Actuator** for health checks and monitoring
- ✅ **Application Metrics** with Prometheus integration
- ✅ **Duplicate Prevention** using database constraints
- ✅ **Input Validation** with proper error handling
- ✅ **Automatic Timestamps** for bets and wins only

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL 15**
- **Maven 3.9**
- **Docker & Docker Compose**
- **OpenAPI/Swagger UI**
- **JUnit 5 & Mockito**
- **TestContainers**

## Quick Start

### Prerequisites

- Docker and Docker Compose installed
- Java 17+ (only if running locally without Docker)

### Run with Docker Compose (Recommended)

#### Option 1: Quick Start Script (Windows)
```powershell
.\start.ps1
```
This script will:
1. Start all services automatically
2. Wait for the application to be ready
3. Open Swagger UI in your default browser
4. Display all available URLs

#### Option 2: Manual Docker Compose
```bash
docker compose up --build
```

#### Stop Services
```powershell
.\stop.ps1
```
or
```bash
docker compose down
```

### API Documentation

Once the application is running, access the interactive API documentation:
- **OpenAPI/Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Actuator Health**: http://localhost:8080/actuator/health
- **Application Metrics**: http://localhost:8080/api/metrics/custom
- **System Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus

## API Endpoints

### Health Check & Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Spring Boot Actuator health endpoint |
| GET | `/api/metrics/custom` | Custom application metrics |
| GET | `/actuator/metrics` | System metrics |
| GET | `/actuator/prometheus` | Prometheus format metrics |

### Jackpots

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/jackpots` | Create a new jackpot |
| GET | `/api/jackpots` | Get all jackpots |

**Create Jackpot Request Body:**
```json
{
  "name": "Mega Jackpot",
  "winProbability": 0.01
}
```

### Bets

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/jackpots/{id}/bet` | Place a bet on a jackpot |
| GET | `/api/wins` | Get all wins |

**Place Bet Request Body:**
```json
{
  "playerAlias": "john_doe",
  "betAmount": 10.0
}
```

## Example Usage

### 1. Create a Jackpot

```bash
curl -X POST http://localhost:8080/api/jackpots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Super Mega Jackpot",
    "winProbability": 0.15
  }'
```

Response:
```json
{
  "id": 1,
  "name": "Super Mega Jackpot",
  "currentSize": 0.00,
  "numberOfWins": 0,
  "lastWinTimestamp": null
}
```

### 2. Place a Bet

```bash
curl -X POST http://localhost:8080/api/jackpots/1/bet \
  -H "Content-Type: application/json" \
  -d '{
    "playerAlias": "john_doe",
    "betAmount": 10.50
  }'
```

Response:
```json
{
  "betId": 1,
  "jackpotId": 1,
  "playerAlias": "john_doe",
  "betAmount": 10.50,
  "isWinner": true,
  "message": "Congratulations! You won!",
  "timestamp": "2025-09-26T10:31:00"
}
```

## Local Development

### Run without Docker

1. **Start PostgreSQL database:**
   ```bash
   docker run --name jackpot-postgres -e POSTGRES_DB=jackpot_db -e POSTGRES_USER=jackpot_user -e POSTGRES_PASSWORD=jackpot_password -p 5432:5432 -d postgres:15-alpine
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

### Run Tests

#### Option 1: Using Docker (Recommended - No Java setup needed)
```bash
# Run all tests using Maven in Docker container
docker run --rm -v ${PWD}:/app -w /app maven:3.9-eclipse-temurin-17 mvn test

# Run only unit tests (excludes integration tests)
docker run --rm -v ${PWD}:/app -w /app maven:3.9-eclipse-temurin-17 mvn test -Dtest="**/*Test" -DfailIfNoTests=false

# Run only service tests
docker run --rm -v ${PWD}:/app -w /app maven:3.9-eclipse-temurin-17 mvn test -Dtest="**/*ServiceTest,**/*RepositoryTest,**/*ControllerTest" -DfailIfNoTests=false
```

#### Option 2: Local Maven (Requires Java 17+ configured in PATH)
```bash
# Run all tests
./mvnw test

# Run only unit tests
./mvnw test -Dtest="**/*Test" -DfailIfNoTests=false

# Run only service and controller tests
./mvnw test -Dtest="**/*ServiceTest,**/*ControllerTest" -DfailIfNoTests=false
```

#### Fix Java PATH on Windows (if needed)
If you get "java command not found" error:
1. Find your Java installation: `where java` or check `C:\Program Files\Java\`
2. Add to System PATH: `C:\Program Files\Java\jdk-17\bin`
3. Or use Docker option above (no Java setup needed)

## Database Schema

### Jackpots Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-generated identifier |
| name | VARCHAR(255) | Unique jackpot name |
| win_probability | DECIMAL(38,2) | Win probability (0.0-1.0) |
| current_size | DECIMAL(38,2) | Current jackpot size |
| win_count | INTEGER | Number of wins |
| last_win_timestamp | TIMESTAMP | Last win timestamp |

### Bets Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-generated identifier |
| jackpot_id | BIGINT (FK) | Reference to jackpots table |
| player_alias | VARCHAR(255) | Player's alias |
| bet_amount | DECIMAL(38,2) | Bet amount |
| timestamp | TIMESTAMP | Bet placement timestamp (auto-generated) |

### Wins Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-generated identifier |
| jackpot_id | BIGINT (FK) | Reference to jackpots table |
| player_alias | VARCHAR(255) | Winner's alias |
| win_amount | DECIMAL(38,2) | Amount won |
| timestamp | TIMESTAMP | Win timestamp (auto-generated) |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| SPRING_DATASOURCE_URL | jdbc:postgresql://localhost:5432/jackpot_db | Database URL |
| SPRING_DATASOURCE_USERNAME | jackpot_user | Database username |
| SPRING_DATASOURCE_PASSWORD | jackpot_password | Database password |
| SERVER_PORT | 8080 | Application port |

### Application Properties

Key configuration properties in `application.properties`:
- Database connection settings
- JPA/Hibernate configuration
- OpenAPI documentation settings
- Server port configuration
- Actuator endpoints configuration
- Prometheus metrics configuration

## Monitoring & Metrics

The application provides comprehensive monitoring and metrics capabilities:

### Health Checks

**Spring Boot Actuator Health** (`/actuator/health`) - Comprehensive health monitoring:
- **Database connectivity**: Automatic PostgreSQL connection validation
- **Disk space**: File system availability checks  
- **Application status**: Overall service health
- **Component-level details**: Individual component health status
- **Readiness indicators**: Service ready to handle traffic
- **Liveness probes**: Service is running and responsive

### Application Metrics

**Custom Business Metrics** (`/api/metrics/custom`):
```json
{
  "total_jackpots": 5,
  "total_bets_processed": 150,
  "total_amount_wagered": "15750.00",
  "total_wins": 23,
  "total_amount_won": "125400.00",
  "active_players": 42
}
```

### System Metrics

**Actuator Metrics** (`/actuator/metrics`):
- JVM metrics (memory, threads, GC)
- HTTP request metrics
- Database connection pool metrics
- Custom application counters

**Prometheus Integration** (`/actuator/prometheus`):
- Prometheus-format metrics export
- Ready for Grafana integration
- Time-series data collection

## 🔍 **What does Spring Boot Actuator do?**

Spring Boot Actuator is a **monitoring and management library** that provides production-ready endpoints for monitoring and managing Spring Boot applications.

### **Key Features:**

#### 1. **Automatic Health Checks** (`/actuator/health`)
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250685575168,
        "free": 86753456128,
        "threshold": 10485760,
        "path": "/app"
      }
    }
  }
}
```

#### 2. **System Metrics** (`/actuator/metrics`)
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.threads.live", 
    "http.server.requests",
    "hikaricp.connections.active",
    "system.cpu.usage"
  ]
}
```

#### 3. **Application Information** (`/actuator/info`)
- Application version
- Build details 
- Environment information

#### 4. **Configuration Management** (`/actuator/env`)
- Environment variables
- Application properties
- Active profiles

#### 5. **Dynamic Logging** (`/actuator/loggers`)
- Change log levels in real time
- View current logging configuration

### **Production Benefits:**

1. **Proactive Monitoring**: Detect issues before they affect users
2. **Debugging**: Detailed information for troubleshooting  
3. **Alerting**: Integration with monitoring systems (Prometheus + Grafana)
4. **Compliance**: Meet observability requirements in enterprise environments
5. **DevOps**: Facilitates CI/CD and automated deployment

### **Security:**

```properties
# Secure configuration for production
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.security.enabled=true
```

### Entity Design & Timestamps

The application follows a **streamlined entity design** with timestamps only where needed:

#### **Jackpots Entity**
- ✅ **No automatic timestamps** (`createdAt`/`updatedAt` removed)
- ✅ **Business-specific timestamp**: `lastWinTimestamp` for tracking last win
- ✅ **Simplified structure**: Focus on core jackpot data

#### **Bets & Wins Entities**
- ✅ **Single timestamp field**: `timestamp` with `@CreationTimestamp`
- ✅ **Automatic generation**: No manual timestamp management needed
- ✅ **Immutable records**: Bets and wins don't change after creation

**Why this design?**
- **Jackpots**: Don't need creation/update tracking, only business events (wins)
- **Bets/Wins**: Need precise timing for auditing and duplicate prevention
- **Simpler maintenance**: Fewer fields to manage and validate
- **Performance**: Reduced database overhead and query complexity

### Duplicate Prevention

The application implements robust duplicate prevention using database constraints:

```sql
-- Unique constraint prevents duplicate bets
ALTER TABLE bets ADD CONSTRAINT unique_bet 
  UNIQUE (player_alias, jackpot_id, timestamp);
```

**Why this approach prevents duplicates:**
- **Database-level guarantee**: Constraint enforced at DB level, not application level
- **Race condition safe**: Multiple concurrent requests cannot create duplicates
- **Atomic operation**: Insert succeeds or fails atomically
- **Microsecond precision**: Timestamp precision makes collision extremely rare
- **Fail-fast**: Duplicate attempts fail immediately with constraint violation

This is simpler and more reliable than complex idempotency services for this use case.

### ⚠️ **Limitation: Multiple Requests Due to Network Issues**

**Problem Scenario:**
If a user has internet problems and clicks "bet" multiple times because the response is slow, **multiple bets can be processed** since each request has a different `timestamp`.

**Example:**
```
User clicks "Bet $10" at 14:30:15.001
User clicks "Bet $10" at 14:30:15.503  ← Different timestamp!
User clicks "Bet $10" at 14:30:16.102  ← Different timestamp!

Result: 3 separate $10 bets processed = $30 total
```

**Current Constraint:**
```sql
UNIQUE (player_alias, jackpot_id, timestamp)
```
✅ Prevents: Same user, same jackpot, **same exact timestamp**
❌ Allows: Same user, same jackpot, **different timestamps**

**Potential Solutions (Not Implemented):**
1. **Request ID Header**: Client generates UUID for each logical bet
2. **Time Window**: Prevent bets within X seconds from same user
3. **Frontend Debouncing**: Disable button after first click
4. **Session Tokens**: One-time-use tokens per bet attempt

**Current Status:** 
This is a **business decision** - the current implementation prioritizes simplicity and allows multiple legitimate bets over complex deduplication logic.

## Testing

The project includes comprehensive testing:

### Unit Tests
- **Service Layer**: Business logic validation
- **Controller Layer**: HTTP request/response handling
- **Repository Layer**: Data access operations

### Integration Tests
- **Full API Workflow**: End-to-end testing
- **Database Integration**: Real database operations using H2
- **Error Handling**: Exception scenarios and validation

### Test Configuration
- Uses H2 in-memory database for fast test execution
- Test-specific application properties
- MockMvc for web layer testing
- TestContainers for integration tests (when needed)

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │────│     Service     │────│   Repository    │
│     Layer       │    │     Layer       │    │     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   DTO/Request   │    │   Business      │    │   JPA Entity    │
│   Response      │    │   Logic         │    │   PostgreSQL    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Project Structure

```
src/
├── main/
│   ├── java/com/jackpot/game/
│   │   ├── JackpotGameApplication.java
│   │   ├── controller/
│   │   │   ├── JackpotController.java
│   │   │   └── BetController.java
│   │   ├── service/
│   │   │   ├── JackpotService.java
│   │   │   └── BetService.java
│   │   ├── repository/
│   │   │   ├── JackpotRepository.java
│   │   │   └── BetRepository.java
│   │   ├── entity/
│   │   │   ├── Jackpot.java
│   │   │   └── Bet.java
│   │   ├── dto/
│   │   │   ├── CreateJackpotRequest.java
│   │   │   ├── JackpotResponse.java
│   │   │   ├── PlaceBetRequest.java
│   │   │   └── BetResponse.java
│   │   └── exception/
│   │       ├── JackpotNotFoundException.java
│   │       ├── JackpotAlreadyExistsException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/jackpot/game/
    │   ├── service/
    │   │   ├── JackpotServiceTest.java
    │   │   └── BetServiceTest.java
    │   ├── controller/
    │   │   └── JackpotControllerTest.java
    │   └── integration/
    │       └── JackpotGameIntegrationTest.java
    └── resources/
        └── application-test.properties
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.
