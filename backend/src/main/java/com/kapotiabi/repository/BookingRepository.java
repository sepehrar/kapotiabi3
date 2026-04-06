package com.kapotiabi.repository;

import com.kapotiabi.entity.BookingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
  List<BookingEntity> findByUserId(Long userId);

  List<BookingEntity> findByShopId(Long shopId);

  Optional<BookingEntity> findByOfferId(Long offerId);
}
