package com.kapotiabi.controller;

import com.kapotiabi.dto.AuthResponse;
import com.kapotiabi.dto.ChangePasswordRequest;
import com.kapotiabi.dto.ForgotPasswordRequest;
import com.kapotiabi.dto.LoginRequest;
import com.kapotiabi.dto.RegisterRequest;
import com.kapotiabi.dto.ResetPasswordRequest;
import com.kapotiabi.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    return ResponseEntity.ok(authService.login(req));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, String>> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest req) {
    return ResponseEntity.ok(authService.forgotPassword(req));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest req) {
    return ResponseEntity.ok(authService.resetPassword(req));
  }

  @PutMapping("/me/password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
    Long userId =
        Long.parseLong(
            (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    authService.changePassword(userId, req);
    return ResponseEntity.noContent().build();
  }
}
