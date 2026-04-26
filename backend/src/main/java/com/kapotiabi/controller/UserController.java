package com.kapotiabi.controller;

import com.kapotiabi.dto.request.UpdateUserRequest;
import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  @Operation(summary = "Get current user profile")
  public ResponseEntity<UserResponse> getMe() {
    return ResponseEntity.ok(userService.getById(SecurityUtils.getAuthUserId()));
  }

  @PutMapping("/me")
  @Operation(summary = "Update current user profile")
  public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest req) {
    return ResponseEntity.ok(userService.update(SecurityUtils.getAuthUserId(), req));
  }
}
