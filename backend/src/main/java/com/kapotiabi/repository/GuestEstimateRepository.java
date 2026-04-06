package com.kapotiabi.repository;

import com.kapotiabi.entity.GuestEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestEstimateRepository extends JpaRepository<GuestEstimate, Long> {
}
