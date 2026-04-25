# Kapotiabi

Car repair marketplace for Estonia. Car owners describe their problem, get price estimates from repair shops, and book appointments.

## Documentation

- [Authentication (login, register, password reset)](docs/auth.md)
- [Saved Shops (bookmarks)](docs/saved-shops.md)

Auth endpoints:
- `POST /api/v1/auth/register` — register a new user
- `POST /api/v1/auth/login` — log in and receive a JWT token
- `POST /api/v1/auth/forgot-password` — request a password reset email
- `POST /api/v1/auth/reset-password` — reset password using a token

Saved shops endpoints:
- `GET /api/v1/users/{userId}/saved-shops` — list saved shops
- `POST /api/v1/users/{userId}/saved-shops/{shopId}` — save a shop
- `DELETE /api/v1/users/{userId}/saved-shops/{shopId}` — remove a saved shop

## Running Locally

```bash
# Backend
cd backend
SPRING_DATASOURCE_PASSWORD=kapotiabi ./mvnw spring-boot:run

# Run tests
cd backend
./mvnw test
```

See [docs/auth.md](docs/auth.md) for full setup instructions and API reference.

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

| Variable  | Initial value           |
|-----------|-------------------------|
| `baseUrl` | `http://localhost:8080` |
| `token`   | *(leave empty)*         |

Use `{{baseUrl}}` and `{{token}}` in your requests.

### 3. Typical end-to-end flow

#### Step 1 — Register & log in

```http
POST {{baseUrl}}/api/v1/auth/register
```
```json
{
  "email": "mari@example.ee",
  "password": "Test1234",
  "firstName": "Mari",
  "lastName": "Mets",
  "role": "CAR_OWNER"
}
```

```http
POST {{baseUrl}}/api/v1/auth/login
```
```json
{
  "email": "mari@example.ee",
  "password": "Test1234"
}
```

Auto-capture the token in the login request's **Tests** tab:
```js
pm.environment.set("token", pm.response.json().token);
```

All subsequent requests require the header: `Authorization: Bearer {{token}}`

---

#### Step 2 — Add a car

```http
POST {{baseUrl}}/api/v1/cars
Authorization: Bearer {{token}}
```
```json
{
  "plateNumber": "123ABC",
  "make": "Toyota",
  "model": "Corolla",
  "year": 2019,
  "engine": "1.6 petrol"
}
```

> Owner is taken from the JWT token — do not send `ownerId` in the body.

---

#### Step 3 — Create a job request

```http
POST {{baseUrl}}/api/v1/job-requests
Authorization: Bearer {{token}}
```
```json
{
  "carId": 1,
  "description": "Oil change + brake inspection",
  "searchScope": "city",
  "city": "Tallinn",
  "offerDeadline": "2026-04-10T18:00:00"
}
```

> User is taken from the JWT token — do not send `userId` in the body.

---

#### Step 4 — Register a repair shop (second user)

Register a second account with `"role": "SHOP_OWNER"`, then create the shop:

```http
POST {{baseUrl}}/api/v1/shops
Authorization: Bearer {{token}}
```
```json
{
  "name": "Tallinn Auto",
  "city": "Tallinn",
  "status": "active"
}
```

> Owner is taken from the JWT token — do not send `ownerId` in the body.

---

#### Step 5 — Submit an offer

```http
POST {{baseUrl}}/api/v1/job-requests/1/offers
```
```json
{
  "shopId": 1,
  "priceEstimate": 85.00,
  "breakdown": "Oil 40€, labour 45€",
  "partsTier": "OEM",
  "estimatedDurationHours": 1.5,
  "validUntil": "2026-04-11T18:00:00"
}
```

---

#### Step 6 — Accept the offer (car owner)

```http
POST {{baseUrl}}/api/v1/offers/1/accept
```

This creates a booking automatically.

---

#### Step 7 — Shop confirms and schedules

```http
POST {{baseUrl}}/api/v1/bookings/1/confirm
POST {{baseUrl}}/api/v1/bookings/1/schedule?scheduledAt=2026-04-12T10:00:00
```

---

#### Step 8 — Complete and leave a review

```http
POST {{baseUrl}}/api/v1/bookings/1/complete
```

```http
POST {{baseUrl}}/api/v1/bookings/1/review
```
```json
{
  "rating": 5,
  "comment": "Fast and professional service."
}
```

---

#### Guest estimate (no account needed)

```http
POST {{baseUrl}}/api/v1/guest-estimates
```
```json
{
  "description": "Brake pad replacement",
  "city": "Tallinn"
}
```

### 4. Upload a PDF offer (multipart)

```http
POST {{baseUrl}}/api/v1/offers/1/pdf
```

Body: `form-data`, key: `file` (type: File), select a `.pdf` file.

```http
GET {{baseUrl}}/api/v1/offers/1/pdf
```

## Database

PostgreSQL is managed via Docker. Schema changes use Flyway migrations (`backend/src/main/resources/db/migration/`).

> **Note on `db/init.sql`:** This file seeds the Docker DB on first run. If the V1 migration schema changes (e.g. BIGSERIAL/BIGINT updates), you must reset the DB volume:
> ```bash
> docker-compose down -v && docker-compose up --build
> ```
