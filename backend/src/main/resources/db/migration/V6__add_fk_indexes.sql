CREATE INDEX idx_cars_owner_id ON cars (owner_id);

CREATE INDEX idx_repair_shops_owner_id ON repair_shops (owner_id);

CREATE INDEX idx_shop_service_categories_shop_id ON shop_service_categories (shop_id);

CREATE INDEX idx_job_requests_user_id ON job_requests (user_id);
CREATE INDEX idx_job_requests_car_id ON job_requests (car_id);

CREATE INDEX idx_shop_offers_job_request_id ON shop_offers (job_request_id);
CREATE INDEX idx_shop_offers_shop_id ON shop_offers (shop_id);

CREATE INDEX idx_bookings_job_request_id ON bookings (job_request_id);
CREATE INDEX idx_bookings_user_id ON bookings (user_id);
CREATE INDEX idx_bookings_shop_id ON bookings (shop_id);

CREATE INDEX idx_reviews_user_id ON reviews (user_id);
CREATE INDEX idx_reviews_shop_id ON reviews (shop_id);

CREATE INDEX idx_guest_estimate_results_estimate_id ON guest_estimate_results (guest_estimate_id);
CREATE INDEX idx_guest_estimate_results_shop_id ON guest_estimate_results (shop_id);
