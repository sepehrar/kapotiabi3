package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ShopServiceCategoryRequest(
    @NotBlank @Size(max = 100) String category,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    @Size(max = 50) String partsTier) {}
