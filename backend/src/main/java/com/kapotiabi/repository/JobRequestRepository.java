package com.kapotiabi.repository;

import com.kapotiabi.entity.JobRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {
    List<JobRequest> findByUserId(Long userId);
    List<JobRequest> findByStatusAndCity(String status, String city);
    List<JobRequest> findByStatusAndCounty(String status, String county);
    List<JobRequest> findByStatus(String status);
}
