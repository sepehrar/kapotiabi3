package com.kapotiabi.repository;

import com.kapotiabi.entity.GuestEstimateResultEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestEstimateResultRepository
    extends JpaRepository<GuestEstimateResultEntity, Long> {
  List<GuestEstimateResultEntity> findByGuestEstimateId(Long guestEstimateId);
}
