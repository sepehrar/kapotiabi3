package com.kapotiabi.repository;

import com.kapotiabi.entity.ShopOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopOfferRepository extends JpaRepository<ShopOffer, Long> {
    List<ShopOffer> findByJobRequestId(Long jobRequestId);
    List<ShopOffer> findByShopId(Long shopId);
}
