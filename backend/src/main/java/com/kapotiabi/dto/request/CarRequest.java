package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CarRequest(
    @NotBlank @Size(max = 20) String plateNumber,
    @Size(max = 100) String make,
    @Size(max = 100) String model,
    Integer year,
    @Size(max = 100) String engine) {}
