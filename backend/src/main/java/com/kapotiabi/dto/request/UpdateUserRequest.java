package com.kapotiabi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank String firstName, @NotBlank String lastName, String phone) {}
