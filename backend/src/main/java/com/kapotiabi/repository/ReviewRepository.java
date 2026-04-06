package com.kapotiabi.repository;

import com.kapotiabi.entity.ReviewEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
  List<ReviewEntity> findByShopId(Long shopId);

  Optional<ReviewEntity> findByBookingId(Long bookingId);
}
