package com.kapotiabi.repository;

import com.kapotiabi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByShopId(Long shopId);
    Optional<Review> findByBookingId(Long bookingId);
}
