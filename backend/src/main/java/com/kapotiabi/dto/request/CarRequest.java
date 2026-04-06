package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarRequest(
        @NotNull Long ownerId,
        @NotBlank String plateNumber,
        String make,
        String model,
        Integer year,
        String engine
) {}
