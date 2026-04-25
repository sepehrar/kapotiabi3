package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShopOfferRequest(
    @NotNull Long shopId,
    @NotNull BigDecimal priceEstimate,
    @Size(max = 2000) String breakdown,
    @Size(max = 50) String partsTier,
    BigDecimal estimatedDurationHours,
    LocalDateTime validUntil) {}
