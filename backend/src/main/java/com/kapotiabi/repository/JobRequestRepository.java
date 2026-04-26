package com.kapotiabi.repository;

import com.kapotiabi.entity.JobRequestEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRequestRepository extends JpaRepository<JobRequestEntity, Long> {
  List<JobRequestEntity> findByUserId(Long userId);

  List<JobRequestEntity> findByStatus(String status);

  Page<JobRequestEntity> findByStatus(String status, Pageable pageable);

  List<JobRequestEntity> findByStatusAndCity(String status, String city);

  Page<JobRequestEntity> findByStatusAndCity(String status, String city, Pageable pageable);

  List<JobRequestEntity> findByStatusAndCounty(String status, String county);

  Page<JobRequestEntity> findByStatusAndCounty(String status, String county, Pageable pageable);
}
