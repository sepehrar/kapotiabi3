package com.kapotiabi.service;

import com.kapotiabi.dto.request.GuestEstimateRequest;
import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.entity.GuestEstimateEntity;
import com.kapotiabi.entity.GuestEstimateResultEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.GuestEstimateRepository;
import com.kapotiabi.repository.GuestEstimateResultRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestEstimateService {

  private final GuestEstimateRepository estimateRepository;
  private final GuestEstimateResultRepository resultRepository;
  private final ShopServiceCategoryRepository categoryRepository;
  private final RepairShopRepository shopRepository;

  @Transactional
  public GuestEstimateResponse create(GuestEstimateRequest req) {
    GuestEstimateEntity estimate =
        GuestEstimateEntity.builder()
            .plateNumber(req.plateNumber())
            .description(req.description())
            .city(req.city())
            .county(req.county())
            .createdAt(LocalDateTime.now())
            .build();
    estimate = estimateRepository.save(estimate);

    List<RepairShopEntity> shops;
    if (req.city() != null) {
      shops = shopRepository.findByStatusAndCity("active", req.city());
    } else if (req.county() != null) {
      shops = shopRepository.findByStatusAndCounty("active", req.county());
    } else {
      shops = shopRepository.findByStatus("active");
    }

    BigDecimal minPrice = null;
    BigDecimal maxPrice = null;

    for (RepairShopEntity shop : shops) {
      var categories = categoryRepository.findByShopId(shop.getId());
      if (categories.isEmpty()) continue;

      BigDecimal shopMin =
          categories.stream()
              .map(c -> c.getPriceFrom())
              .filter(p -> p != null)
              .min(BigDecimal::compareTo)
              .orElse(null);
      BigDecimal shopMax =
          categories.stream()
              .map(c -> c.getPriceTo())
              .filter(p -> p != null)
              .max(BigDecimal::compareTo)
              .orElse(null);

      GuestEstimateResultEntity result =
          GuestEstimateResultEntity.builder()
              .guestEstimateId(estimate.getId())
              .shopId(shop.getId())
              .priceFrom(shopMin)
              .priceTo(shopMax)
              .build();
      resultRepository.save(result);

      if (shopMin != null && (minPrice == null || shopMin.compareTo(minPrice) < 0))
        minPrice = shopMin;
      if (shopMax != null && (maxPrice == null || shopMax.compareTo(maxPrice) > 0))
        maxPrice = shopMax;
    }

    estimate.setPriceFrom(minPrice);
    estimate.setPriceTo(maxPrice);
    estimate = estimateRepository.save(estimate);

    List<GuestEstimateResultEntity> results =
        resultRepository.findByGuestEstimateId(estimate.getId());
    return GuestEstimateResponse.from(estimate, results);
  }

  public GuestEstimateResponse getById(Long id) {
    GuestEstimateEntity estimate =
        estimateRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Estimate not found: " + id));
    List<GuestEstimateResultEntity> results = resultRepository.findByGuestEstimateId(id);
    return GuestEstimateResponse.from(estimate, results);
  }
}
