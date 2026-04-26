package com.kapotiabi.dto.response;

import com.kapotiabi.entity.BookingEntity;
import java.time.LocalDateTime;

public record BookingResponse(
    Long id,
    Long offerId,
    Long jobRequestId,
    Long userId,
    Long shopId,
    LocalDateTime scheduledAt,
    Boolean confirmedByShop,
    LocalDateTime shopConfirmedAt,
    String status,
    LocalDateTime createdAt) {
  public static BookingResponse from(BookingEntity e) {
    return new BookingResponse(
        e.getId(),
        e.getOffer().getId(),
        e.getJobRequest().getId(),
        e.getUser().getId(),
        e.getShop().getId(),
        e.getScheduledAt(),
        e.getConfirmedByShop(),
        e.getShopConfirmedAt(),
        e.getStatus(),
        e.getCreatedAt());
  }
}
