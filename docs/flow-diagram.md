# Product Flow Diagram

## Car Owner Demo Flow

```mermaid
flowchart TD
  A[Open search] --> B[Describe car problem in chat]
  B --> C[Answer follow-up questions]
  C --> D[See matching service cards]
  D --> E[Select 2-3 services for compare]
  E --> F[Open /account/compare]
  F --> G[Choose service]
  G --> H[Select booking slot]
  H --> I[Confirm modal]
  I --> J[Booking saved]
  J --> K[Open /account/bookings]
```

## Backend Booking Flow

```mermaid
flowchart TD
  A[Car owner creates job request] --> B[Shop owner submits offer]
  B --> C[Offer includes slots]
  C --> D[Car owner accepts offer with slotId]
  D --> E[Backend creates booking]
  E --> F[Shop confirms booking]
  F --> G[Booking can be completed or cancelled]
```

## Current Demo Notes

- Search results and compare choices are session-backed for the demo.
- Booking confirmation uses backend only when real `offerId` and `slotId` exist.
- Otherwise, a session booking is created so the full UI flow can be shown.
