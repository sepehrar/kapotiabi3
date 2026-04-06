package com.kapotiabi.repository;

import com.kapotiabi.entity.RepairShopEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairShopRepository extends JpaRepository<RepairShopEntity, Long> {
  List<RepairShopEntity> findByOwnerId(Long ownerId);

  List<RepairShopEntity> findByCity(String city);

  List<RepairShopEntity> findByCounty(String county);

  List<RepairShopEntity> findByStatus(String status);

  List<RepairShopEntity> findByStatusAndCity(String status, String city);

  List<RepairShopEntity> findByStatusAndCounty(String status, String county);
}
