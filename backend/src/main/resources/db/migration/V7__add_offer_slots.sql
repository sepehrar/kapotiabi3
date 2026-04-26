CREATE TABLE offer_slots (
    id         BIGSERIAL PRIMARY KEY,
    offer_id   BIGINT        NOT NULL REFERENCES shop_offers (id) ON DELETE CASCADE,
    starts_at  TIMESTAMP     NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_offer_slots_offer_id ON offer_slots (offer_id);
