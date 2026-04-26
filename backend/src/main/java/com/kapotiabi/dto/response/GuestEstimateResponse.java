package com.kapotiabi.dto.response;

import com.kapotiabi.entity.GuestEstimateEntity;
import com.kapotiabi.entity.GuestEstimateResultEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GuestEstimateResponse(
    Long id,
    String description,
    String city,
    String county,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    LocalDateTime createdAt,
    List<GuestEstimateResultResponse> results) {
  public record GuestEstimateResultResponse(
      Long shopId, BigDecimal priceFrom, BigDecimal priceTo) {}

  public static GuestEstimateResponse from(
      GuestEstimateEntity e, List<GuestEstimateResultEntity> results) {
    List<GuestEstimateResultResponse> resultResponses =
        results.stream()
            .map(
                r ->
                    new GuestEstimateResultResponse(
                        r.getShop().getId(), r.getPriceFrom(), r.getPriceTo()))
            .toList();
    return new GuestEstimateResponse(
        e.getId(),
        e.getDescription(),
        e.getCity(),
        e.getCounty(),
        e.getPriceFrom(),
        e.getPriceTo(),
        e.getCreatedAt(),
        resultResponses);
  }
}
