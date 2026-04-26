package com.kapotiabi.dto.response;

import com.kapotiabi.entity.OfferSlotEntity;
import com.kapotiabi.entity.ShopOfferEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    LocalDateTime createdAt,
    List<OfferSlotResponse> slots) {

  public static ShopOfferResponse from(ShopOfferEntity e, List<OfferSlotEntity> slots) {
    return new ShopOfferResponse(
        e.getId(),
        e.getJobRequestId(),
        e.getShopId(),
        e.getPriceEstimate(),
        e.getBreakdown(),
        e.getPartsTier(),
        e.getEstimatedDurationHours(),
        e.getValidUntil(),
        e.getPdfUrl(),
        e.getStatus(),
        e.getCreatedAt(),
        slots.stream().map(OfferSlotResponse::from).toList());
  }
}
