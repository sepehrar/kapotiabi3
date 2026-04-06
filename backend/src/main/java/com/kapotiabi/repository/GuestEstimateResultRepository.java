package com.kapotiabi.repository;

import com.kapotiabi.entity.GuestEstimateResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestEstimateResultRepository extends JpaRepository<GuestEstimateResult, Long> {
    List<GuestEstimateResult> findByGuestEstimateId(Long guestEstimateId);
}
