package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record JobRequestRequest(
    @NotNull Long userId,
    @NotNull Long carId,
    @NotBlank String description,
    Boolean acceptsNonOem,
    String partsPreference,
    String searchScope,
    String city,
    String county,
    LocalDateTime offerDeadline) {}
