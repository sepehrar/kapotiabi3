package com.kapotiabi.repository;

import com.kapotiabi.entity.BookingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
  List<BookingEntity> findByUserId(Long userId);

  Page<BookingEntity> findByUserId(Long userId, Pageable pageable);

  List<BookingEntity> findByShopId(Long shopId);

  Page<BookingEntity> findByShopId(Long shopId, Pageable pageable);

  Optional<BookingEntity> findByOfferId(Long offerId);
}
