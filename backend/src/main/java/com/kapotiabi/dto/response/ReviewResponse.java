package com.kapotiabi.dto.response;

import com.kapotiabi.entity.ReviewEntity;
import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long bookingId,
    Long userId,
    Long shopId,
    Integer rating,
    String comment,
    LocalDateTime createdAt) {
  public static ReviewResponse from(ReviewEntity e) {
    return new ReviewResponse(
        e.getId(),
        e.getBooking().getId(),
        e.getUser().getId(),
        e.getShop().getId(),
        e.getRating(),
        e.getComment(),
        e.getCreatedAt());
  }
}
