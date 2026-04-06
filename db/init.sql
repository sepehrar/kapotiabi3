-- ───────────────────────────────────────────
-- TalTech Car Repair Marketplace — DB Schema
-- ───────────────────────────────────────────

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    role          VARCHAR NOT NULL CHECK (role IN ('car_owner', 'shop_owner', 'admin')),
    first_name    VARCHAR,
    last_name     VARCHAR,
    phone         VARCHAR,
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);

CREATE TABLE cars (
    id           BIGSERIAL PRIMARY KEY,
    owner_id     BIGINT NOT NULL REFERENCES users(id),
    plate_number VARCHAR NOT NULL,
    make         VARCHAR,
    model        VARCHAR,
    year         INTEGER,
    engine       VARCHAR,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TABLE repair_shops (
    id              BIGSERIAL PRIMARY KEY,
    owner_id        BIGINT NOT NULL REFERENCES users(id),
    name            VARCHAR NOT NULL,
    address         VARCHAR,
    city            VARCHAR,
    county          VARCHAR,
    phone           VARCHAR,
    email           VARCHAR,
    website         VARCHAR,
    google_maps_url VARCHAR,
    status          VARCHAR CHECK (status IN ('active', 'draft', 'needs_update')),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE shop_service_categories (
    id           BIGSERIAL PRIMARY KEY,
    shop_id      BIGINT NOT NULL REFERENCES repair_shops(id),
    category     VARCHAR NOT NULL,  -- oil_change | tire_change | diagnostics | bodywork | etc
    price_from   DECIMAL,
    price_to     DECIMAL,
    parts_tier   VARCHAR,
    last_updated TIMESTAMP DEFAULT NOW()
);

CREATE TABLE job_requests (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    car_id           BIGINT NOT NULL REFERENCES cars(id),
    description      TEXT NOT NULL,
    accepts_non_oem  BOOLEAN DEFAULT FALSE,
    parts_preference VARCHAR,
    search_scope     VARCHAR CHECK (search_scope IN ('city', 'county', 'country')),
    city             VARCHAR,
    county           VARCHAR,
    ai_summary       TEXT,
    status           VARCHAR CHECK (status IN ('pending', 'offers_received', 'booked', 'completed', 'cancelled')),
    offer_deadline   TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);

CREATE TABLE shop_offers (
    id                       BIGSERIAL PRIMARY KEY,
    job_request_id           BIGINT NOT NULL REFERENCES job_requests(id),
    shop_id                  BIGINT NOT NULL REFERENCES repair_shops(id),
    price_estimate           DECIMAL NOT NULL,
    breakdown                TEXT,
    parts_tier               VARCHAR CHECK (parts_tier IN ('oem', 'equivalent', 'budget')),
    estimated_duration_hours DECIMAL,
    valid_until              TIMESTAMP,
    pdf_url                  VARCHAR,
    status                   VARCHAR CHECK (status IN ('pending', 'accepted', 'rejected', 'expired')),
    created_at               TIMESTAMP DEFAULT NOW()
);

CREATE TABLE bookings (
    id                BIGSERIAL PRIMARY KEY,
    offer_id          BIGINT NOT NULL UNIQUE REFERENCES shop_offers(id),
    job_request_id    BIGINT NOT NULL REFERENCES job_requests(id),
    user_id           BIGINT NOT NULL REFERENCES users(id),
    shop_id           BIGINT NOT NULL REFERENCES repair_shops(id),
    scheduled_at      TIMESTAMP,
    confirmed_by_shop BOOLEAN DEFAULT FALSE,
    shop_confirmed_at TIMESTAMP,
    status            VARCHAR CHECK (status IN ('awaiting_shop', 'confirmed', 'in_progress', 'completed', 'cancelled')),
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

CREATE TABLE reviews (
    id         BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE REFERENCES bookings(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    shop_id    BIGINT NOT NULL REFERENCES repair_shops(id),
    rating     INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE guest_estimates (
    id           BIGSERIAL PRIMARY KEY,
    plate_number VARCHAR,
    description  TEXT,
    city         VARCHAR,
    county       VARCHAR,
    price_from   DECIMAL,
    price_to     DECIMAL,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TABLE guest_estimate_results (
    id                BIGSERIAL PRIMARY KEY,
    guest_estimate_id BIGINT NOT NULL REFERENCES guest_estimates(id),
    shop_id           BIGINT NOT NULL REFERENCES repair_shops(id),
    price_from        DECIMAL,
    price_to          DECIMAL
);

-- Indexes
CREATE INDEX ON cars(owner_id);
CREATE INDEX ON repair_shops(owner_id);
CREATE INDEX ON shop_service_categories(shop_id);
CREATE INDEX ON job_requests(user_id);
CREATE INDEX ON job_requests(car_id);
CREATE INDEX ON shop_offers(job_request_id);
CREATE INDEX ON shop_offers(shop_id);
CREATE INDEX ON bookings(job_request_id);
CREATE INDEX ON bookings(user_id);
CREATE INDEX ON bookings(shop_id);
CREATE INDEX ON reviews(shop_id);
CREATE INDEX ON guest_estimate_results(guest_estimate_id);
CREATE INDEX ON guest_estimate_results(shop_id);
