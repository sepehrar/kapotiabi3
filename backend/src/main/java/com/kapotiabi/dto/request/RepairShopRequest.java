package com.kapotiabi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairShopRequest(
    @NotNull Long ownerId,
    @NotBlank @Size(max = 200) String name,
    @Size(max = 500) String address,
    @Size(max = 100) String city,
    @Size(max = 100) String county,
    @Size(max = 20) String phone,
    @Email @Size(max = 255) String email,
    @Size(max = 200) String website,
    @Size(max = 500) String googleMapsUrl,
    String status) {}
