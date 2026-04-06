package com.kapotiabi.dto.response;

import com.kapotiabi.entity.ShopOffer;

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
        LocalDateTime createdAt
) {
    public static ShopOfferResponse from(ShopOffer o) {
        return new ShopOfferResponse(o.getId(), o.getJobRequestId(), o.getShopId(),
                o.getPriceEstimate(), o.getBreakdown(), o.getPartsTier(),
                o.getEstimatedDurationHours(), o.getValidUntil(), o.getPdfUrl(),
                o.getStatus(), o.getCreatedAt());
    }
}
