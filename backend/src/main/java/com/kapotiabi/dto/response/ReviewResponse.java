package com.kapotiabi.dto.response;

import com.kapotiabi.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long bookingId,
        Long userId,
        Long shopId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(r.getId(), r.getBookingId(), r.getUserId(),
                r.getShopId(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
