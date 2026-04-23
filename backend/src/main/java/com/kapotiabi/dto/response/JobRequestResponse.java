package com.kapotiabi.dto.response;

import com.kapotiabi.entity.JobRequestEntity;
import java.time.LocalDateTime;

public record JobRequestResponse(
    Long id,
    Long userId,
    Long carId,
    String description,
    Boolean acceptsNonOem,
    String partsPreference,
    String searchScope,
    String city,
    String county,
    String aiSummary,
    String status,
    LocalDateTime offerDeadline,
    LocalDateTime createdAt) {
  public static JobRequestResponse from(JobRequestEntity e) {
    return new JobRequestResponse(
        e.getId(),
        e.getUser().getId(),
        e.getCar().getId(),
        e.getDescription(),
        e.getAcceptsNonOem(),
        e.getPartsPreference(),
        e.getSearchScope(),
        e.getCity(),
        e.getCounty(),
        e.getAiSummary(),
        e.getStatus(),
        e.getOfferDeadline(),
        e.getCreatedAt());
  }
}
