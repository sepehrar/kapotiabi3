package com.kapotiabi.dto.response;

import com.kapotiabi.entity.ShopServiceCategoryEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShopServiceCategoryResponse(
    Long id,
    Long shopId,
    String category,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    String partsTier,
    LocalDateTime lastUpdated) {
  public static ShopServiceCategoryResponse from(ShopServiceCategoryEntity e) {
    return new ShopServiceCategoryResponse(
        e.getId(),
        e.getShop().getId(),
        e.getCategory(),
        e.getPriceFrom(),
        e.getPriceTo(),
        e.getPartsTier(),
        e.getLastUpdated());
  }
}
