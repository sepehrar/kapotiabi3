package com.kapotiabi.dto.response;

import com.kapotiabi.entity.JobRequest;

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
        LocalDateTime createdAt
) {
    public static JobRequestResponse from(JobRequest r) {
        return new JobRequestResponse(r.getId(), r.getUserId(), r.getCarId(),
                r.getDescription(), r.getAcceptsNonOem(), r.getPartsPreference(),
                r.getSearchScope(), r.getCity(), r.getCounty(), r.getAiSummary(),
                r.getStatus(), r.getOfferDeadline(), r.getCreatedAt());
    }
}
