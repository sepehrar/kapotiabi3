# Offer Slots — Backend Documentation

## Overview

When a shop submits an offer for a job request, they can include a list of available time slots. The car owner sees these slots on the booking page, picks one, and confirms. The chosen slot's `startsAt` becomes the booking's `scheduledAt` — set at the moment of acceptance, not later by the shop.

---

## Flow

```
Shop creates offer  →  includes available slots (e.g. Mon 10:00, Mon 14:00, Tue 09:00)
User views offer    →  picks a slot from the calendar
User accepts offer  →  sends chosen slotId  →  booking created with scheduledAt set
Shop confirms       →  booking status: confirmed
```

---

## API Endpoints

### POST `/api/v1/job-requests/{id}/offers`

Shop submits an offer with optional time slots.

**Request:**
```json
{
  "priceEstimate": 95.00,
  "breakdown": "Oil + filter change. OEM-equivalent parts.",
  "partsTier": "oem-eq",
  "estimatedDurationHours": 1.5,
  "validUntil": "2026-05-10T23:59:00",
  "slots": [
    "2026-05-06T10:00:00",
    "2026-05-06T14:00:00",
    "2026-05-07T09:00:00"
  ]
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `priceEstimate` | Yes | |
| `breakdown` | No | Max 2000 characters |
| `partsTier` | No | Max 100 characters |
| `estimatedDurationHours` | No | |
| `validUntil` | No | Offer expires at this datetime |
| `slots` | No | List of ISO-8601 datetimes — the times the shop is available |

**Response `201 Created`:**
```json
{
  "id": 5,
  "jobRequestId": 12,
  "shopId": 3,
  "priceEstimate": 95.00,
  "breakdown": "Oil + filter change. OEM-equivalent parts.",
  "partsTier": "oem-eq",
  "estimatedDurationHours": 1.5,
  "validUntil": "2026-05-10T23:59:00",
  "pdfUrl": null,
  "status": "pending",
  "createdAt": "2026-04-26T10:00:00",
  "slots": [
    { "id": 1, "startsAt": "2026-05-06T10:00:00" },
    { "id": 2, "startsAt": "2026-05-06T14:00:00" },
    { "id": 3, "startsAt": "2026-05-07T09:00:00" }
  ]
}
```

---

### GET `/api/v1/offers/{id}`

Returns the offer including its slots. Used by the frontend booking page.

**Response `200 OK`:** same shape as the create response above.

---

### POST `/api/v1/offers/{id}/accept`

Car owner accepts the offer and picks a time slot. Creates a booking.

**Request:**
```json
{
  "slotId": 2
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `slotId` | Yes | Must be a slot that belongs to this offer |

**Response `200 OK`:**
```json
{
  "id": 8,
  "offerId": 5,
  "jobRequestId": 12,
  "userId": 4,
  "shopId": 3,
  "scheduledAt": "2026-05-06T14:00:00",
  "confirmedByShop": false,
  "shopConfirmedAt": null,
  "status": "awaiting_shop",
  "createdAt": "2026-04-26T10:05:00",
  "updatedAt": "2026-04-26T10:05:00"
}
```

Note: `scheduledAt` is set immediately from the chosen slot — the shop does not need to schedule separately.

**Error responses:**
| Status | Reason |
|--------|--------|
| 400 | `slotId` is missing |
| 403 | Slot does not belong to this offer |
| 403 | Authenticated user does not own the job request |
| 409 | A booking already exists for this offer |
| 422 | Offer has expired (`validUntil` is in the past) |

---

### PUT `/api/v1/offers/{id}`

Shop updates an offer. Providing a `slots` list **replaces** all existing slots for that offer.

**Request:** same shape as create. Omitting `slots` or sending `null` clears all existing slots.

---

## Database

Migration `V7__add_offer_slots.sql`:

```sql
CREATE TABLE offer_slots (
    id         BIGSERIAL PRIMARY KEY,
    offer_id   BIGINT    NOT NULL REFERENCES shop_offers (id) ON DELETE CASCADE,
    starts_at  TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_offer_slots_offer_id ON offer_slots (offer_id);
```

Slots are deleted automatically (`ON DELETE CASCADE`) when the parent offer is deleted.

---

## Running Tests

From the `backend/` directory:

```bash
# Run offer and booking service tests
./mvnw test -Dtest="ShopOfferServiceTest,BookingServiceTest"

# Run all tests
./mvnw test
```

**Tests added for this feature:**
- `ShopOfferServiceTest` — `getById_includesSlots`, `create_withSlots_savesSlots`, `create_withoutSlots_returnsEmptySlots`, `update_replacesSlots`, `getSlotOrThrow_notFound`
- `BookingServiceTest` — `createFromOffer_success` (asserts `scheduledAt` set), `createFromOffer_slotBelongsToDifferentOffer_throwsForbidden`
