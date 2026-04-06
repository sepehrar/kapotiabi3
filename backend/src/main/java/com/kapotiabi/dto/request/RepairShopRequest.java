package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RepairShopRequest(
    @NotNull Long ownerId,
    @NotBlank String name,
    String address,
    String city,
    String county,
    String phone,
    String email,
    String website,
    String googleMapsUrl,
    String status) {}
