package com.kapotiabi.dto.response;

import com.kapotiabi.entity.OfferSlotEntity;
import java.time.LocalDateTime;

public record OfferSlotResponse(Long id, LocalDateTime startsAt) {

  public static OfferSlotResponse from(OfferSlotEntity e) {
    return new OfferSlotResponse(e.getId(), e.getStartsAt());
  }
}
