package com.kapotiabi.service;

import com.kapotiabi.dto.request.GuestEstimateRequest;
import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.entity.GuestEstimateEntity;
import com.kapotiabi.entity.GuestEstimateResultEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopServiceCategoryEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.GuestEstimateRepository;
import com.kapotiabi.repository.GuestEstimateResultRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestEstimateService {

  private final GuestEstimateRepository guestEstimateRepository;
  private final GuestEstimateResultRepository resultRepository;
  private final RepairShopRepository repairShopRepository;
  private final ShopServiceCategoryRepository categoryRepository;

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
    estimate = guestEstimateRepository.save(estimate);

    List<RepairShopEntity> shops;
    if (req.city() != null) {
      shops = repairShopRepository.findByStatusAndCity("active", req.city());
    } else if (req.county() != null) {
      shops = repairShopRepository.findByStatusAndCounty("active", req.county());
    } else {
      shops = repairShopRepository.findByStatus("active");
    }

    BigDecimal overallMin = null;
    BigDecimal overallMax = null;

    List<Long> shopIds = shops.stream().map(RepairShopEntity::getId).toList();
    Map<Long, List<ShopServiceCategoryEntity>> categoriesByShop =
        categoryRepository.findByShopIdIn(shopIds).stream()
            .collect(Collectors.groupingBy(c -> c.getShop().getId()));

    for (RepairShopEntity shop : shops) {
      List<ShopServiceCategoryEntity> categories =
          categoriesByShop.getOrDefault(shop.getId(), List.of());
      if (categories.isEmpty()) {
        continue;
      }

      Optional<BigDecimal> shopMin =
          categories.stream()
              .map(ShopServiceCategoryEntity::getPriceFrom)
              .filter(Objects::nonNull)
              .min(Comparator.naturalOrder());
      Optional<BigDecimal> shopMax =
          categories.stream()
              .map(ShopServiceCategoryEntity::getPriceTo)
              .filter(Objects::nonNull)
              .max(Comparator.naturalOrder());

      GuestEstimateResultEntity result =
          GuestEstimateResultEntity.builder()
              .guestEstimate(estimate)
              .shop(shop)
              .priceFrom(shopMin.orElse(null))
              .priceTo(shopMax.orElse(null))
              .build();
      resultRepository.save(result);

      if (shopMin.isPresent()) {
        overallMin = overallMin == null ? shopMin.get() : overallMin.min(shopMin.get());
      }
      if (shopMax.isPresent()) {
        overallMax = overallMax == null ? shopMax.get() : overallMax.max(shopMax.get());
      }
    }

    estimate.setPriceFrom(overallMin);
    estimate.setPriceTo(overallMax);
    estimate = guestEstimateRepository.save(estimate);

    return GuestEstimateResponse.from(
        estimate, resultRepository.findByGuestEstimateId(estimate.getId()));
  }

  public GuestEstimateResponse getById(Long id) {
    GuestEstimateEntity estimate =
        guestEstimateRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Guest estimate not found: " + id));
    return GuestEstimateResponse.from(estimate, resultRepository.findByGuestEstimateId(id));
  }
}
