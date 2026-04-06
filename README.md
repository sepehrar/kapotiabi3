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
| Auth            | `POST /api/v1/auth/register` · `POST /api/v1/auth/login` · `POST /api/v1/auth/forgot-password` · `POST /api/v1/auth/reset-password` |
| Users           | `GET /api/v1/users/{id}` · `PUT /api/v1/users/{id}` |
| Cars            | `CRUD /api/v1/cars` |
| Repair Shops    | `CRUD /api/v1/shops` + categories + reviews |
| Job Requests    | `CRUD /api/v1/job-requests` + offer submission |
| Shop Offers     | `GET /PUT /accept /reject /pdf /api/v1/offers/{id}` |
| Bookings        | `GET /confirm /schedule /complete /cancel /api/v1/bookings/{id}` |
| Reviews         | `POST /GET /api/v1/bookings/{id}/review` |
| Guest Estimates | `POST /GET /api/v1/guest-estimates` |

---

## Testing with Postman

### 1. Import the OpenAPI spec

The fastest way to get all endpoints into Postman:

1. Start the app (`docker-compose up --build`)
2. In Postman: **Import → Link**
3. Enter: `http://localhost:8080/v3/api-docs`
4. Postman generates a collection with all endpoints pre-filled

Alternatively, browse interactively at `http://localhost:8080/swagger-ui/index.html`.

### 2. Set up an environment

Create a Postman environment named **Kapotiabi Local** with these variables:

| Variable   | Initial value             |
|------------|---------------------------|
| `baseUrl`  | `http://localhost:8080`   |
| `token`    | *(leave empty)*           |

Use `{{baseUrl}}` and `{{token}}` in your requests.

### 3. Typical end-to-end flow

#### Register & log in

```
POST {{baseUrl}}/api/v1/auth/register
Content-Type: application/json

{
  "email": "mari@example.ee",
  "password": "Test1234",
  "firstName": "Mari",
  "lastName": "Mets",
  "role": "car_owner"
}
```

```
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json

{
  "email": "mari@example.ee",
  "password": "Test1234"
}
```

Copy the `token` from the login response. In Postman, set `token` in your environment. All subsequent requests need:

```
Authorization: Bearer {{token}}
```

You can automate this: in the **login** request's **Tests** tab add:
```js
pm.environment.set("token", pm.response.json().token);
```

#### Add a car

```
POST {{baseUrl}}/api/v1/cars
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "ownerId": 1,
  "plateNumber": "123ABC",
  "make": "Toyota",
  "model": "Corolla",
  "year": 2019,
  "engine": "1.6 petrol"
}
```

Save the returned `id` as `{{carId}}` in your environment.

#### Create a job request

```
POST {{baseUrl}}/api/v1/job-requests
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "userId": 1,
  "carId": 1,
  "description": "Oil change + brake inspection",
  "searchScope": "city",
  "city": "Tallinn",
  "offerDeadline": "2026-04-10T18:00:00"
}
```

#### Register a repair shop and submit an offer

Register a second user as `shop_owner`, then:

```
POST {{baseUrl}}/api/v1/shops
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "ownerId": 2,
  "name": "Tallinn Auto",
  "city": "Tallinn",
  "status": "active"
}
```

```
POST {{baseUrl}}/api/v1/job-requests/1/offers
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "shopId": 1,
  "priceEstimate": 85.00,
  "breakdown": "Oil 40€, labour 45€",
  "partsTier": "OEM",
  "estimatedDurationHours": 1.5,
  "validUntil": "2026-04-11T18:00:00"
}
```

#### Accept the offer → creates a booking

```
POST {{baseUrl}}/api/v1/offers/1/accept
Authorization: Bearer {{token}}
```

#### Shop confirms and schedules

```
POST {{baseUrl}}/api/v1/bookings/1/confirm
Authorization: Bearer {{token}}
```

```
POST {{baseUrl}}/api/v1/bookings/1/schedule?scheduledAt=2026-04-12T10:00:00
Authorization: Bearer {{token}}
```

#### Complete and leave a review

```
POST {{baseUrl}}/api/v1/bookings/1/complete
Authorization: Bearer {{token}}
```

```
POST {{baseUrl}}/api/v1/bookings/1/review
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "rating": 5,
  "comment": "Fast and professional service."
}
```

#### Guest estimate (no account needed)

```
POST {{baseUrl}}/api/v1/guest-estimates
Content-Type: application/json

{
  "description": "Brake pad replacement",
  "city": "Tallinn"
}
```

### 4. Upload a PDF offer (multipart)

In Postman, set the request to `POST {{baseUrl}}/api/v1/offers/1/pdf`:
- Body → **form-data**
- Key: `file`, Type: **File**, Value: select a `.pdf` from your machine

Download it back with `GET {{baseUrl}}/api/v1/offers/1/pdf`.

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

**41 tests** across 7 service test classes + 1 context load test. All use JUnit 5 + Mockito (no DB required for unit tests).

```
BookingServiceTest        — 7 tests
UserServiceTest           — 3 tests
CarServiceTest            — 6 tests
RepairShopServiceTest     — 7 tests
JobRequestServiceTest     — 6 tests
ShopOfferServiceTest      — 5 tests
ReviewServiceTest         — 6 tests
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

| Variable                     | Default                                       | Description        |
|------------------------------|-----------------------------------------------|--------------------|
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/kapotiabi`   | DB connection URL  |
| `SPRING_DATASOURCE_USERNAME` | `kapotiabi`                                   | DB username        |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)*                                     | DB password        |
| `APP_UPLOAD_DIR`             | `uploads`                                     | PDF storage path   |

---

## Git Workflow

- `main` — production-ready only
- `develop` — integration branch, all features merge here
- `feature/<description>` — feature branches
- `fix/<description>` — bugfix branches
- PRs require at least 1 review before merging to `develop`
