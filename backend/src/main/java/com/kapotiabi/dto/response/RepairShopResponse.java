package com.kapotiabi.dto.response;

import com.kapotiabi.entity.RepairShopEntity;
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
    LocalDateTime createdAt) {
  public static RepairShopResponse from(RepairShopEntity e) {
    return new RepairShopResponse(
        e.getId(),
        e.getOwner().getId(),
        e.getName(),
        e.getAddress(),
        e.getCity(),
        e.getCounty(),
        e.getPhone(),
        e.getEmail(),
        e.getWebsite(),
        e.getGoogleMapsUrl(),
        e.getStatus(),
        e.getCreatedAt());
  }
}
