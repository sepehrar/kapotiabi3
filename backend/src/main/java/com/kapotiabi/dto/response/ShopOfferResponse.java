package com.kapotiabi.dto.response;

import com.kapotiabi.entity.ShopOfferEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShopOfferResponse(
    Long id,
    Long jobRequestId,
    Long shopId,
    BigDecimal priceEstimate,
    String breakdown,
    String partsTier,
    BigDecimal estimatedDurationHours,
    LocalDateTime validUntil,
    String pdfUrl,
    String status,
    LocalDateTime createdAt) {
  public static ShopOfferResponse from(ShopOfferEntity e) {
    return new ShopOfferResponse(
        e.getId(),
        e.getJobRequest().getId(),
        e.getShop().getId(),
        e.getPriceEstimate(),
        e.getBreakdown(),
        e.getPartsTier(),
        e.getEstimatedDurationHours(),
        e.getValidUntil(),
        e.getPdfUrl(),
        e.getStatus(),
        e.getCreatedAt());
  }
}
