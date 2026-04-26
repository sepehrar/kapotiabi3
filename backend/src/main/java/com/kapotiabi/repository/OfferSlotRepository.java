package com.kapotiabi.repository;

import com.kapotiabi.entity.OfferSlotEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferSlotRepository extends JpaRepository<OfferSlotEntity, Long> {

  List<OfferSlotEntity> findByOfferId(Long offerId);
}
