package com.kapotiabi.repository;

import com.kapotiabi.entity.ShopServiceCategoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopServiceCategoryRepository
    extends JpaRepository<ShopServiceCategoryEntity, Long> {
  List<ShopServiceCategoryEntity> findByShopId(Long shopId);
}
