package com.kapotiabi.repository;

import com.kapotiabi.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByShopId(Long shopId);
    Optional<Booking> findByOfferId(Long offerId);
}
