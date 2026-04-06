package com.kapotiabi.repository;

import com.kapotiabi.entity.JobRequestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRequestRepository extends JpaRepository<JobRequestEntity, Long> {
  List<JobRequestEntity> findByUserId(Long userId);

  List<JobRequestEntity> findByStatusAndCity(String status, String city);

  List<JobRequestEntity> findByStatusAndCounty(String status, String county);

  List<JobRequestEntity> findByStatus(String status);
}
