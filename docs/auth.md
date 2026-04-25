# Authentication — Backend Documentation

## Overview

The auth system handles registration, login, and password reset for all user types. After a successful login or register, the server returns a **JWT token**. The frontend stores this token and sends it with every future request to prove who the user is.

---

## User Roles

| Role | How They Get In | Notes |
|------|----------------|-------|
| `car_owner` | Self-registers | Normal user |
| `shop_owner` | Self-registers, picks "shop owner" | A blank shop is auto-created |
| `admin` | Cannot self-register | Pre-seeded in DB via migration |

---

## API Endpoints

Base path: `/api/v1/auth`

All endpoints that require a body accept and return `application/json`.

---

### POST `/api/v1/auth/register`

Register a new car owner or shop owner.

**Request:**
```json
{
  "email": "jaan@test.ee",
  "password": "Test123",
  "firstName": "Jaan",
  "lastName": "Tamm",
  "phone": "+372 5555 1234",
  "role": "SHOP_OWNER"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `email` | Yes | Must be valid email, must be unique |
| `password` | Yes | Minimum 6 characters |
| `firstName` | Yes | |
| `lastName` | Yes | |
| `phone` | No | |
| `role` | Yes | `CAR_OWNER` or `SHOP_OWNER` — `ADMIN` is rejected |

**Response `201 Created`:**
```json
{
  "token": "eyJhbGci...",
  "expiresAt": "2026-04-07T08:00:00Z",
  "role": "shop_owner",
  "userId": 3,
  "shopStatus": "draft"
}
```

`shopStatus` is `null` for `car_owner`. For `shop_owner` it starts as `draft` until an admin publishes the shop.

**Error responses:**
| Status | Reason |
|--------|--------|
| 400 | `ADMIN` role attempted, missing fields, invalid email, short password |
| 409 | Email already registered |

---

### POST `/api/v1/auth/login`

Log in as any role (car owner, shop owner, or admin).

**Request:**
```json
{
  "email": "jaan@test.ee",
  "password": "Test123"
}
```

**Response `200 OK`:** same shape as register response.

**Error responses:**
| Status | Reason |
|--------|--------|
| 401 | Wrong email or password |

---

### POST `/api/v1/auth/forgot-password`

Request a password reset token.

**Request:**
```json
{
  "email": "jaan@test.ee"
}
```

**Response `200 OK`:**
```json
{
  "message": "If that email exists, a reset link has been sent."
}
```

The response is the same whether or not the email exists — this prevents leaking which emails are registered.

> **MVP note:** The reset token is logged to the server console instead of being emailed. Check the application logs for the token.

---

### PUT `/api/v1/auth/me/password`

Change the password for the currently logged-in user. Requires a valid JWT.

**Request:**
```json
{
  "currentPassword": "OldPass123",
  "newPassword": "NewPass456"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `currentPassword` | Yes | Must match the user's existing password |
| `newPassword` | Yes | Minimum 6, maximum 72 characters. Must differ from `currentPassword` |

**Response `204 No Content`**

**Error responses:**
| Status | Reason |
|--------|--------|
| 400 | `newPassword` is the same as `currentPassword` |
| 401 | `currentPassword` is wrong |
| 404 | User not found |

---

### POST `/api/v1/auth/reset-password`

Reset the password using a token from the forgot-password step.

**Request:**
```json
{
  "token": "uuid-from-console-log",
  "newPassword": "NewPass456"
}
```

**Response `200 OK`:**
```json
{
  "message": "Password updated successfully."
}
```

**Error responses:**
| Status | Reason |
|--------|--------|
| 400 | Token not found, already used, or expired (1 hour expiry) |

---

## JWT Token

The token returned after login/register is a signed JWT. It contains:

```json
{
  "sub": "3",
  "email": "jaan@test.ee",
  "role": "shop_owner",
  "iat": 1775465194,
  "exp": 1775551594
}
```

Include it in every authenticated request:

```
Authorization: Bearer eyJhbGci...
```

Token is valid for **24 hours**. Configurable via `JWT_EXPIRATION_MS` environment variable.

---

## Admin Account

The first admin is seeded automatically by Flyway migration `V3__seed_admin.sql`:

| Field | Value |
|-------|-------|
| Email | `admin@kapotiabi.ee` |
| Password | `Admin1234` |

**Change the password after first login** using the forgot-password flow.

Future admins can be promoted in-app by existing admins (admin panel — future work).

---

## How Shop Owner Registration Works

When a shop owner registers:
1. A `users` row is created with `role = shop_owner`
2. A `repair_shops` row is automatically created with `status = draft`
3. The shop is not visible to the public until an admin approves it (`status = active`)
4. An admin can also reject the shop (`status = rejected`) — rejected shops stay invisible

**Shop statuses:**

| Status | Meaning |
|--------|---------|
| `draft` | Newly created, awaiting admin review |
| `active` | Approved — visible in search results and job request matching |
| `rejected` | Rejected by admin — not visible, owner should contact support |

---

## Error Response Format

All errors return the same structure:

```json
{
  "timestamp": "2026-04-06T08:46:44Z",
  "status": 409,
  "message": "Email already registered",
  "path": "/api/v1/auth/register"
}
```

---

## Running Tests

From the `backend/` directory:

```bash
# Run all tests
./mvnw test

# Run only auth service tests
./mvnw test -Dtest=AuthServiceTest

# Run only auth controller tests
./mvnw test -Dtest=AuthControllerTest

# Run a single test method
./mvnw test -Dtest=AuthServiceTest#register_adminRole_throwsIllegalArgument
```

**114 tests total:**
- `AuthServiceTest` (19) — business logic: registration rules, password hashing, token lifecycle
- `AuthControllerTest` (18) — HTTP layer: status codes, validation, error responses
- `KapotiabiApplicationTests` (1) — application context loads
- `UserSavedShopServiceTest` (8), `UserSavedShopControllerTest` (8) — saved shops feature
- `BookingServiceTest` (17), `CarServiceTest` (6), `JobRequestServiceTest` (7), `RepairShopServiceTest` (8), `ReviewServiceTest` (5), `ShopOfferServiceTest` (14), `UserServiceTest` (3) — domain service layer

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/kapotiabi` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `kapotiabi` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)* | DB password |
| `JWT_SECRET` | `dev-secret-key-...` | JWT signing key — **change in production** |
| `JWT_EXPIRATION_MS` | `86400000` | Token lifetime in ms (default 24h) |

---

## Running Locally

```bash
# 1. Create the database (first time only)
psql -U postgres -c "CREATE USER kapotiabi WITH PASSWORD 'kapotiabi';"
psql -U postgres -c "CREATE DATABASE kapotiabi OWNER kapotiabi;"

# 2. Start the app — Flyway runs all migrations automatically
cd backend
SPRING_DATASOURCE_PASSWORD=kapotiabi ./mvnw spring-boot:run
```

Flyway runs on startup:
- `V1` — creates all 10 tables
- `V2` — creates `password_reset_tokens` table
- `V3` — seeds the admin user
