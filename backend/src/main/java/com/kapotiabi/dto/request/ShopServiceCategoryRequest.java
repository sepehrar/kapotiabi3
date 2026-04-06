package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ShopServiceCategoryRequest(
        @NotBlank String category,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        String partsTier
) {}
