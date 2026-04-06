package com.kapotiabi.repository;

import com.kapotiabi.entity.ShopOfferEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOfferRepository extends JpaRepository<ShopOfferEntity, Long> {
  List<ShopOfferEntity> findByJobRequestId(Long jobRequestId);

  List<ShopOfferEntity> findByShopId(Long shopId);
}
