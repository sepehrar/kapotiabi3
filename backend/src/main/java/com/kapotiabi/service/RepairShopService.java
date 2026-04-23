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
import com.kapotiabi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepairShopService {

  private final RepairShopRepository repairShopRepository;
  private final ShopServiceCategoryRepository categoryRepository;
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;

  public List<RepairShopResponse> getAll(String city, String county) {
    List<RepairShopEntity> shops;
    if (city != null) {
      shops = repairShopRepository.findByCity(city);
    } else if (county != null) {
      shops = repairShopRepository.findByCounty(county);
    } else {
      shops = repairShopRepository.findAll();
    }
    return shops.stream().map(RepairShopResponse::from).toList();
  }

  public RepairShopResponse getById(Long id) {
    return RepairShopResponse.from(findOrThrow(id));
  }

  public RepairShopResponse create(RepairShopRequest req) {
    var now = LocalDateTime.now();
    var shop =
        RepairShopEntity.builder()
            .owner(userRepository.getReferenceById(req.ownerId()))
            .name(req.name())
            .address(req.address())
            .city(req.city())
            .county(req.county())
            .phone(req.phone())
            .email(req.email())
            .website(req.website())
            .googleMapsUrl(req.googleMapsUrl())
            .status(req.status())
            .createdAt(now)
            .updatedAt(now)
            .build();
    return RepairShopResponse.from(repairShopRepository.save(shop));
  }

  public RepairShopResponse update(Long id, RepairShopRequest req) {
    var shop = findOrThrow(id);
    shop.setOwner(userRepository.getReferenceById(req.ownerId()));
    shop.setName(req.name());
    shop.setAddress(req.address());
    shop.setCity(req.city());
    shop.setCounty(req.county());
    shop.setPhone(req.phone());
    shop.setEmail(req.email());
    shop.setWebsite(req.website());
    shop.setGoogleMapsUrl(req.googleMapsUrl());
    shop.setStatus(req.status());
    shop.setUpdatedAt(LocalDateTime.now());
    return RepairShopResponse.from(repairShopRepository.save(shop));
  }

  public List<ShopServiceCategoryResponse> getCategories(Long shopId) {
    return categoryRepository.findByShopId(shopId).stream()
        .map(ShopServiceCategoryResponse::from)
        .toList();
  }

  public ShopServiceCategoryResponse addCategory(Long shopId, ShopServiceCategoryRequest req) {
    var shop = findOrThrow(shopId);
    var category =
        ShopServiceCategoryEntity.builder()
            .shop(shop)
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
    var category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    if (!shopId.equals(category.getShop().getId())) {
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
    var category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    if (!shopId.equals(category.getShop().getId())) {
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
