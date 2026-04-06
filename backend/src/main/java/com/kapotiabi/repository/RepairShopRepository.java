package com.kapotiabi.repository;

import com.kapotiabi.entity.RepairShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairShopRepository extends JpaRepository<RepairShop, Long> {
    List<RepairShop> findByOwnerId(Long ownerId);
    List<RepairShop> findByCity(String city);
    List<RepairShop> findByCounty(String county);
}
