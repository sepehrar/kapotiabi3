package com.kapotiabi.service;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopServiceCategoryEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ReviewRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepairShopService {

  private final RepairShopRepository repairShopRepository;
  private final ShopServiceCategoryRepository categoryRepository;
  private final ReviewRepository reviewRepository;

  public Page<RepairShopResponse> getAll(String city, String county, Pageable pageable) {
    if (city != null) {
      return repairShopRepository
          .findByStatusAndCity("active", city, pageable)
          .map(RepairShopResponse::from);
    } else if (county != null) {
      return repairShopRepository
          .findByStatusAndCounty("active", county, pageable)
          .map(RepairShopResponse::from);
    } else {
      return repairShopRepository.findByStatus("active", pageable).map(RepairShopResponse::from);
    }
  }

  public List<RepairShopResponse> getAllForAdmin(String status) {
    List<RepairShopEntity> shops =
        status != null ? repairShopRepository.findByStatus(status) : repairShopRepository.findAll();
    return shops.stream().map(RepairShopResponse::from).toList();
  }

  public RepairShopResponse updateShopStatus(Long id, String action) {
    RepairShopEntity shop = findOrThrow(id);
    if (!"draft".equals(shop.getStatus())) {
      throw new IllegalStateException(
          "Shop " + id + " is not in draft status (current: " + shop.getStatus() + ")");
    }
    shop.setStatus("approve".equals(action) ? "active" : "rejected");
    return RepairShopResponse.from(repairShopRepository.save(shop));
  }

  public RepairShopResponse getById(Long id) {
    return RepairShopResponse.from(findOrThrow(id));
  }

  public RepairShopResponse create(RepairShopRequest req) {
    RepairShopEntity shop =
        RepairShopEntity.builder()
            .ownerId(req.ownerId())
            .name(req.name())
            .address(req.address())
            .city(req.city())
            .county(req.county())
            .phone(req.phone())
            .email(req.email())
            .website(req.website())
            .googleMapsUrl(req.googleMapsUrl())
            .status(req.status())
            .build();
    return RepairShopResponse.from(repairShopRepository.save(shop));
  }

  public RepairShopResponse update(Long id, RepairShopRequest req) {
    RepairShopEntity shop = findOrThrow(id);
    shop.setOwnerId(req.ownerId());
    shop.setName(req.name());
    shop.setAddress(req.address());
    shop.setCity(req.city());
    shop.setCounty(req.county());
    shop.setPhone(req.phone());
    shop.setEmail(req.email());
    shop.setWebsite(req.website());
    shop.setGoogleMapsUrl(req.googleMapsUrl());
    shop.setStatus(req.status());
    return RepairShopResponse.from(repairShopRepository.save(shop));
  }

  public List<ShopServiceCategoryResponse> getCategories(Long shopId) {
    return categoryRepository.findByShopId(shopId).stream()
        .map(ShopServiceCategoryResponse::from)
        .toList();
  }

  public ShopServiceCategoryResponse addCategory(Long shopId, ShopServiceCategoryRequest req) {
    findOrThrow(shopId);
    ShopServiceCategoryEntity category =
        ShopServiceCategoryEntity.builder()
            .shopId(shopId)
            .category(req.category())
            .priceFrom(req.priceFrom())
            .priceTo(req.priceTo())
            .partsTier(req.partsTier())
            .lastUpdated(LocalDateTime.now())
            .build();
    return ShopServiceCategoryResponse.from(categoryRepository.save(category));
  }

  public ShopServiceCategoryResponse updateCategory(
      Long shopId, Long categoryId, ShopServiceCategoryRequest req) {
    findOrThrow(shopId);
    ShopServiceCategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    if (!shopId.equals(category.getShopId())) {
      throw new NotFoundException("Category not found: " + categoryId);
    }
    category.setCategory(req.category());
    category.setPriceFrom(req.priceFrom());
    category.setPriceTo(req.priceTo());
    category.setPartsTier(req.partsTier());
    category.setLastUpdated(LocalDateTime.now());
    return ShopServiceCategoryResponse.from(categoryRepository.save(category));
  }

  public void deleteCategory(Long shopId, Long categoryId) {
    findOrThrow(shopId);
    ShopServiceCategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    if (!shopId.equals(category.getShopId())) {
      throw new NotFoundException("Category not found: " + categoryId);
    }
    categoryRepository.deleteById(categoryId);
  }

  public List<ReviewResponse> getReviews(Long shopId) {
    return reviewRepository.findByShopId(shopId).stream().map(ReviewResponse::from).toList();
  }

  private RepairShopEntity findOrThrow(Long id) {
    return repairShopRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Repair shop not found: " + id));
  }
}
