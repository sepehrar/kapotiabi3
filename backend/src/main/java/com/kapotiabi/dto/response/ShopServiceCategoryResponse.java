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
  public static ShopServiceCategoryResponse from(ShopServiceCategoryEntity c) {
    return new ShopServiceCategoryResponse(
        c.getId(),
        c.getShopId(),
        c.getCategory(),
        c.getPriceFrom(),
        c.getPriceTo(),
        c.getPartsTier(),
        c.getLastUpdated());
  }
}
