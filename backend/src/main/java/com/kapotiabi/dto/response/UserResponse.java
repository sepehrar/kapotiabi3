package com.kapotiabi.dto.response;

import com.kapotiabi.entity.UserEntity;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String email,
    String role,
    String firstName,
    String lastName,
    String phone,
    LocalDateTime createdAt) {
  public static UserResponse from(UserEntity u) {
    return new UserResponse(
        u.getId(),
        u.getEmail(),
        u.getRole(),
        u.getFirstName(),
        u.getLastName(),
        u.getPhone(),
        u.getCreatedAt());
  }
}
