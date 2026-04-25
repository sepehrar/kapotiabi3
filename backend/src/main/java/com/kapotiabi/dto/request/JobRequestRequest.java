package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record JobRequestRequest(
    @NotNull Long userId,
    @NotNull Long carId,
    @NotBlank @Size(max = 2000) String description,
    Boolean acceptsNonOem,
    String partsPreference,
    String searchScope,
    String city,
    String county,
    LocalDateTime offerDeadline) {}
