package com.kapotiabi.repository;

import com.kapotiabi.entity.CarEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {
  List<CarEntity> findByOwnerId(Long ownerId);
}
