package com.kapotiabi.dto.response;

import com.kapotiabi.entity.CarEntity;
import java.time.LocalDateTime;

public record CarResponse(
    Long id,
    Long ownerId,
    String plateNumber,
    String make,
    String model,
    Integer year,
    String engine,
    LocalDateTime createdAt) {
  public static CarResponse from(CarEntity e) {
    return new CarResponse(
        e.getId(),
        e.getOwner().getId(),
        e.getPlateNumber(),
        e.getMake(),
        e.getModel(),
        e.getYear(),
        e.getEngine(),
        e.getCreatedAt());
  }
}
