# Database Diagram

This is the practical model used by the backend. See Flyway migrations in `backend/src/main/resources/db/migration` for exact column definitions.

```mermaid
erDiagram
  USERS ||--o{ CARS : owns
  USERS ||--o{ JOB_REQUESTS : creates
  USERS ||--o{ USER_SAVED_SHOPS : saves
  USERS ||--o| REPAIR_SHOPS : owns

  REPAIR_SHOPS ||--o{ SHOP_SERVICE_CATEGORIES : offers
  REPAIR_SHOPS ||--o{ SHOP_OFFERS : submits
  REPAIR_SHOPS ||--o{ BOOKINGS : receives
  REPAIR_SHOPS ||--o{ USER_SAVED_SHOPS : saved_as

  CARS ||--o{ JOB_REQUESTS : used_for
  JOB_REQUESTS ||--o{ SHOP_OFFERS : receives
  SHOP_OFFERS ||--o{ OFFER_SLOTS : proposes
  SHOP_OFFERS ||--o| BOOKINGS : accepted_as
  JOB_REQUESTS ||--o| BOOKINGS : booked_as
  BOOKINGS ||--o| REVIEWS : reviewed_by

  USERS {
    bigint id
    string email
    string role
  }
  REPAIR_SHOPS {
    bigint id
    bigint owner_id
    string status
  }
  JOB_REQUESTS {
    bigint id
    bigint user_id
    bigint car_id
    string status
  }
  SHOP_OFFERS {
    bigint id
    bigint job_request_id
    bigint shop_id
    string status
  }
  BOOKINGS {
    bigint id
    bigint offer_id
    bigint job_request_id
    bigint user_id
    bigint shop_id
    string status
  }
```

## Key Rules

- Users have roles: car owner, shop owner, or admin.
- A shop owner can own one repair shop.
- A booking is created by accepting a shop offer with a selected offer slot.
- Reviews belong to completed bookings.
