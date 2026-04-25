# Authorization — Backend Documentation

## Overview

Every protected endpoint requires a JWT in the `Authorization: Bearer <token>` header. The token encodes the user's ID and role. The backend uses these to enforce ownership rules — users can only act on resources that belong to them.

---

## Roles and What They Can Do

| Role | Key permissions |
|------|----------------|
| `car_owner` | Create job requests, view/accept/reject offers, cancel bookings, submit reviews |
| `shop_owner` | Submit offers, confirm/schedule/complete bookings, upload PDF offers |
| `admin` | Approve or reject shops (admin endpoints only) |

---

## Admin Endpoints

Base path: `/api/v1/admin` — **ADMIN role required for all.**

### GET `/api/v1/admin/shops`

List all shops. Optionally filter by status.

**Query params:**

| Param | Description |
|-------|-------------|
| `status` | Optional. One of: `draft`, `active`, `rejected` |

**Response `200 OK`:** array of shop objects (same shape as `GET /api/v1/shops/{id}`).

---

### PATCH `/api/v1/admin/shops/{id}/status`

Approve or reject a shop. Only works on shops currently in `draft` status.

**Request:**
```json
{ "action": "approve" }
```

| Value | Result |
|-------|--------|
| `approve` | Shop status → `active` (becomes visible in search) |
| `reject` | Shop status → `rejected` (stays invisible) |

**Response `200 OK`:** updated shop object.

**Error responses:**
| Status | Reason |
|--------|--------|
| 400 | `action` is not `approve` or `reject` |
| 403 | Caller is not an admin |
| 404 | Shop not found |
| 409 | Shop is not in `draft` status |

---

## Offer Ownership Rules

### Submitting an offer — `POST /api/v1/job-requests/{id}/offers`

- Caller must be `SHOP_OWNER` with an existing shop in the system.
- The `shopId` is derived from the authenticated user — the request body `shopId` field is ignored.

### Updating an offer — `PUT /api/v1/offers/{id}`

- Only the shop owner who created the offer can update it.

### Uploading a PDF — `POST /api/v1/offers/{id}/pdf`

- Only the shop owner who created the offer can upload a PDF.

### Accepting an offer — `POST /api/v1/offers/{id}/accept`

- Only the `CAR_OWNER` who owns the job request the offer was submitted for.
- Creates a booking on success.

### Rejecting an offer — `POST /api/v1/offers/{id}/reject`

- Only the `CAR_OWNER` who owns the job request the offer was submitted for.

---

## Booking Ownership Rules

### Get bookings — `GET /api/v1/bookings`

No query params needed. Returns bookings based on the caller's role:
- `CAR_OWNER` → their own bookings
- `SHOP_OWNER` → bookings for their shop

### Confirm — `POST /api/v1/bookings/{id}/confirm`

- `SHOP_OWNER` only, and only if their shop matches `booking.shopId`.
- Booking must be in `awaiting_shop` status.

### Schedule — `POST /api/v1/bookings/{id}/schedule`

- `SHOP_OWNER` only, same shop check.
- Booking must be in `confirmed` status.

### Complete — `POST /api/v1/bookings/{id}/complete`

- `SHOP_OWNER` only, same shop check.
- Booking must be in `confirmed` status.

### Cancel — `POST /api/v1/bookings/{id}/cancel`

- Either the `CAR_OWNER` (booking's user) or the `SHOP_OWNER` (booking's shop).
- Cannot cancel a `completed` or already `cancelled` booking.

### Submit review — `POST /api/v1/bookings/{id}/review`

- Only the `CAR_OWNER` who has the booking.
- Booking must be in `completed` status.
- One review per booking (duplicate check enforced).

---

## Booking Status Lifecycle

```
offer accepted
      │
      ▼
 awaiting_shop  ──────────────────────────────► cancelled
      │
  shop confirms
      │
      ▼
  confirmed  ──────────────────────────────────► cancelled
      │
  shop completes
      │
      ▼
  completed  ──► review submitted
```

---

## Error Responses

All auth errors follow the standard error format:

```json
{
  "timestamp": "2026-04-25T10:00:00Z",
  "status": 403,
  "message": "You don't have access to this booking",
  "path": "/api/v1/bookings/7/confirm"
}
```

| Status | Meaning |
|--------|---------|
| 401 | No token or invalid token |
| 403 | Valid token but wrong role or wrong owner |
| 409 | Valid request but wrong state (e.g. booking already confirmed) |
