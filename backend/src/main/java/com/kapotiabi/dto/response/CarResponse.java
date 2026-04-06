package com.kapotiabi.dto.response;

import com.kapotiabi.entity.Car;

import java.time.LocalDateTime;

public record CarResponse(
        Long id,
        Long ownerId,
        String plateNumber,
        String make,
        String model,
        Integer year,
        String engine,
        LocalDateTime createdAt
) {
    public static CarResponse from(Car c) {
        return new CarResponse(c.getId(), c.getOwnerId(), c.getPlateNumber(),
                c.getMake(), c.getModel(), c.getYear(), c.getEngine(), c.getCreatedAt());
    }
}
