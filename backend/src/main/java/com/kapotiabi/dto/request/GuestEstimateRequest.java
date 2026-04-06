package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuestEstimateRequest(
        String plateNumber,
        @NotBlank String description,
        String city,
        String county
) {}
