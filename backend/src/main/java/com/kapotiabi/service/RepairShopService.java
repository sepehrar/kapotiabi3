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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepairShopService {

  private final RepairShopRepository shopRepository;
  private final ShopServiceCategoryRepository categoryRepository;
  private final ReviewRepository reviewRepository;

  public List<RepairShopResponse> getAll(String city, String county) {
    if (city != null)
      return shopRepository.findByCity(city).stream().map(RepairShopResponse::from).toList();
    if (county != null)
      return shopRepository.findByCounty(county).stream().map(RepairShopResponse::from).toList();
    return shopRepository.findAll().stream().map(RepairShopResponse::from).toList();
  }

  public RepairShopResponse getById(Long id) {
    return RepairShopResponse.from(findOrThrow(id));
  }

  public RepairShopResponse create(RepairShopRequest req) {
    RepairShopEntity shop = new RepairShopEntity();
    applyFields(shop, req);
    shop.setCreatedAt(LocalDateTime.now());
    shop.setUpdatedAt(LocalDateTime.now());
    return RepairShopResponse.from(shopRepository.save(shop));
  }

  public RepairShopResponse update(Long id, RepairShopRequest req) {
    RepairShopEntity shop = findOrThrow(id);
    applyFields(shop, req);
    shop.setUpdatedAt(LocalDateTime.now());
    return RepairShopResponse.from(shopRepository.save(shop));
  }

  public List<ShopServiceCategoryResponse> getCategories(Long shopId) {
    findOrThrow(shopId);
    return categoryRepository.findByShopId(shopId).stream()
        .map(ShopServiceCategoryResponse::from)
        .toList();
  }

  public ShopServiceCategoryResponse addCategory(Long shopId, ShopServiceCategoryRequest req) {
    findOrThrow(shopId);
    ShopServiceCategoryEntity cat = new ShopServiceCategoryEntity();
    cat.setShopId(shopId);
    cat.setCategory(req.category());
    cat.setPriceFrom(req.priceFrom());
    cat.setPriceTo(req.priceTo());
    cat.setPartsTier(req.partsTier());
    cat.setLastUpdated(LocalDateTime.now());
    return ShopServiceCategoryResponse.from(categoryRepository.save(cat));
  }

  public ShopServiceCategoryResponse updateCategory(
      Long shopId, Long categoryId, ShopServiceCategoryRequest req) {
    findOrThrow(shopId);
    ShopServiceCategoryEntity cat =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    cat.setCategory(req.category());
    cat.setPriceFrom(req.priceFrom());
    cat.setPriceTo(req.priceTo());
    cat.setPartsTier(req.partsTier());
    cat.setLastUpdated(LocalDateTime.now());
    return ShopServiceCategoryResponse.from(categoryRepository.save(cat));
  }

  public void deleteCategory(Long shopId, Long categoryId) {
    findOrThrow(shopId);
    categoryRepository.deleteById(categoryId);
  }

  public List<ReviewResponse> getReviews(Long shopId) {
    findOrThrow(shopId);
    return reviewRepository.findByShopId(shopId).stream().map(ReviewResponse::from).toList();
  }

  private void applyFields(RepairShopEntity shop, RepairShopRequest req) {
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
  }

  private RepairShopEntity findOrThrow(Long id) {
    return shopRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Shop not found: " + id));
  }
}
