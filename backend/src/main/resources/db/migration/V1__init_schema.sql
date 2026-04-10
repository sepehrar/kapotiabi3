CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    role          VARCHAR NOT NULL,
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
    manufacture_year INTEGER,
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
    status          VARCHAR,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE shop_service_categories (
    id           BIGSERIAL PRIMARY KEY,
    shop_id      BIGINT NOT NULL REFERENCES repair_shops(id),
    category     VARCHAR NOT NULL,
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
    search_scope     VARCHAR,
    city             VARCHAR,
    county           VARCHAR,
    ai_summary       TEXT,
    status           VARCHAR,
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
    parts_tier               VARCHAR,
    estimated_duration_hours DECIMAL,
    valid_until              TIMESTAMP,
    pdf_url                  VARCHAR,
    status                   VARCHAR,
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
    status            VARCHAR,
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
