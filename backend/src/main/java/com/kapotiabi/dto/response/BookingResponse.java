package com.kapotiabi.dto.response;

import com.kapotiabi.entity.Booking;

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
        LocalDateTime createdAt
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getOfferId(), b.getJobRequestId(),
                b.getUserId(), b.getShopId(), b.getScheduledAt(), b.getConfirmedByShop(),
                b.getShopConfirmedAt(), b.getStatus(), b.getCreatedAt());
    }
}
