package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ShopOfferRequest(
    @NotNull BigDecimal priceEstimate,
    @Size(max = 2000) String breakdown,
    @Size(max = 100) String partsTier,
    BigDecimal estimatedDurationHours,
    LocalDateTime validUntil,
    List<LocalDateTime> slots) {}
