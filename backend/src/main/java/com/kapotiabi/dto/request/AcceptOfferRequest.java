package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotNull;

public record AcceptOfferRequest(@NotNull Long slotId) {}
