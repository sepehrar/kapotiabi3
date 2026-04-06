# Kapotiabi

Car repair marketplace connecting car owners with repair shops in Estonia.
Car owners submit job requests, repair shops submit price offers, owners compare and book.

> TalTech Junior-to-Senior Developer course — Team 23

---

## Core Flow

```
Car owner creates job request
        ↓
Repair shops submit price offers (+ optional PDF)
        ↓
Car owner accepts an offer → booking is created
        ↓
Shop confirms & schedules the appointment
        ↓
Job completed → car owner leaves a review
```

Guests can get a simplified price estimate without registering.

---

## Tech Stack

| Layer     | Technology                                    |
|-----------|-----------------------------------------------|
| Backend   | Java 21, Spring Boot 4, Spring Data JPA       |
| Database  | PostgreSQL (Flyway migrations)                |
| Auth      | Spring Security (JWT — in progress)           |
| Docs      | SpringDoc OpenAPI / Swagger UI                |
| Frontend  | Next.js 14+, Tailwind CSS (in progress)       |
| Infra     | Docker, Docker Compose                        |

---

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 21 (for running backend outside Docker)

### Run everything with Docker

```bash
docker-compose up --build
```

This starts the PostgreSQL database and the Spring Boot backend together.

| Service   | URL                        |
|-----------|----------------------------|
| Backend   | http://localhost:8080      |
| Swagger UI| http://localhost:8080/swagger-ui/index.html |

### Run backend locally (with DB in Docker)

```bash
# Start only the database
docker-compose up db

# Run the backend
cd backend
./mvnw spring-boot:run
```

---

## API Documentation

Swagger UI is available at **http://localhost:8080/swagger-ui/index.html** when the app is running.

Raw OpenAPI spec: `http://localhost:8080/v3/api-docs`

### Endpoint summary

| Group           | Endpoints |
|-----------------|-----------|
| Auth            | `POST /api/v1/auth/register` |
| Users           | `GET /PUT /api/v1/users/{id}` |
| Cars            | `CRUD /api/v1/cars` |
| Repair Shops    | `CRUD /api/v1/shops` + categories + reviews |
| Job Requests    | `CRUD /api/v1/job-requests` + offer submission |
| Shop Offers     | `GET /PUT /accept /reject /pdf /api/v1/offers/{id}` |
| Bookings        | `GET /confirm /schedule /complete /cancel /api/v1/bookings/{id}` |
| Reviews         | `POST /GET /api/v1/bookings/{id}/review` |
| Guest Estimates | `POST /GET /api/v1/guest-estimates` |

---

## Database

PostgreSQL schema is managed by **Flyway**. Migration files live in:

```
backend/src/main/resources/db/migration/
```

Naming convention: `V1__init_schema.sql`, `V2__description.sql`, etc.

Never modify migration files that have already been applied — always add a new migration.

---

## Development

### Running tests

```bash
cd backend
./mvnw test
```

**43 tests** across 7 service test classes + 1 context load test. All use JUnit 5 + Mockito (no DB required for unit tests).

```
BookingServiceTest    — 7 tests
UserServiceTest       — 5 tests
CarServiceTest        — 6 tests
RepairShopServiceTest — 7 tests
JobRequestServiceTest — 6 tests
ShopOfferServiceTest  — 5 tests
ReviewServiceTest     — 6 tests
KapotiabiApplicationTests — 1 (context load)
```

### Backend structure

```
backend/src/main/java/com/kapotiabi/
├── config/       — Security, OpenAPI
├── controller/   — REST endpoints (HTTP layer only)
├── service/      — Business logic
├── repository/   — Spring Data JPA interfaces
├── entity/       — JPA entities (one per DB table)
├── dto/
│   ├── request/  — Validated input records
│   └── response/ — Output records with .from(entity) factories
└── exception/    — NotFoundException, GlobalExceptionHandler
```

### Environment variables

| Variable                 | Default                              | Description         |
|--------------------------|--------------------------------------|---------------------|
| `SPRING_DATASOURCE_URL`  | `jdbc:postgresql://localhost:5432/kapotiabi` | DB connection URL |
| `SPRING_DATASOURCE_USERNAME` | `kapotiabi`                      | DB username         |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)*                        | DB password         |
| `APP_UPLOAD_DIR`         | `uploads`                            | PDF storage path    |

---

## Git Workflow

- `main` — production-ready only
- `develop` — integration branch, all features merge here
- `feature/<description>` — feature branches
- `fix/<description>` — bugfix branches
- PRs require at least 1 review before merging to `develop`
