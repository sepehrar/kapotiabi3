package com.kapotiabi.repository;

import com.kapotiabi.entity.RepairShopEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairShopRepository extends JpaRepository<RepairShopEntity, Long> {
  Optional<RepairShopEntity> findByOwnerId(Long ownerId);

  List<RepairShopEntity> findByCity(String city);

  List<RepairShopEntity> findByCounty(String county);

  List<RepairShopEntity> findByStatus(String status);

  Page<RepairShopEntity> findByStatus(String status, Pageable pageable);

  List<RepairShopEntity> findByStatusAndCity(String status, String city);

  Page<RepairShopEntity> findByStatusAndCity(String status, String city, Pageable pageable);

  List<RepairShopEntity> findByStatusAndCounty(String status, String county);

  Page<RepairShopEntity> findByStatusAndCounty(String status, String county, Pageable pageable);
}
