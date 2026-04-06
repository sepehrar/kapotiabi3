package com.kapotiabi.dto.response;

import com.kapotiabi.entity.GuestEstimate;
import com.kapotiabi.entity.GuestEstimateResult;

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
        List<GuestEstimateResultResponse> results
) {
    public record GuestEstimateResultResponse(Long shopId, BigDecimal priceFrom, BigDecimal priceTo) {
        public static GuestEstimateResultResponse from(GuestEstimateResult r) {
            return new GuestEstimateResultResponse(r.getShopId(), r.getPriceFrom(), r.getPriceTo());
        }
    }

    public static GuestEstimateResponse from(GuestEstimate e, List<GuestEstimateResult> results) {
        return new GuestEstimateResponse(e.getId(), e.getDescription(), e.getCity(), e.getCounty(),
                e.getPriceFrom(), e.getPriceTo(), e.getCreatedAt(),
                results.stream().map(GuestEstimateResultResponse::from).toList());
    }
}
