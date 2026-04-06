package com.kapotiabi.dto.response;

import com.kapotiabi.entity.RepairShop;

import java.time.LocalDateTime;

public record RepairShopResponse(
        Long id,
        Long ownerId,
        String name,
        String address,
        String city,
        String county,
        String phone,
        String email,
        String website,
        String googleMapsUrl,
        String status,
        LocalDateTime createdAt
) {
    public static RepairShopResponse from(RepairShop s) {
        return new RepairShopResponse(s.getId(), s.getOwnerId(), s.getName(),
                s.getAddress(), s.getCity(), s.getCounty(), s.getPhone(),
                s.getEmail(), s.getWebsite(), s.getGoogleMapsUrl(), s.getStatus(), s.getCreatedAt());
    }
}
