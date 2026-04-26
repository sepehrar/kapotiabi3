package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminShopStatusRequest(
    @NotBlank @Pattern(regexp = "approve|reject", message = "action must be 'approve' or 'reject'")
        String action) {}
