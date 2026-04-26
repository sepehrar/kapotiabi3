package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuestEstimateRequest(
    @NotBlank @Size(max = 2000) String description,
    @Size(max = 20) String plateNumber,
    @Size(max = 100) String city,
    @Size(max = 100) String county) {}
