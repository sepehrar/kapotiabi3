package com.kapotiabi.repository;

import com.kapotiabi.entity.ShopServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopServiceCategoryRepository extends JpaRepository<ShopServiceCategory, Long> {
    List<ShopServiceCategory> findByShopId(Long shopId);
}
