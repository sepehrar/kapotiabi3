package com.kapotiabi.service;

import com.kapotiabi.dto.*;
import com.kapotiabi.entity.PasswordResetTokenEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.enums.UserRole;
import com.kapotiabi.exception.DuplicateEmailException;
import com.kapotiabi.exception.InvalidCredentialsException;
import com.kapotiabi.exception.InvalidTokenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.PasswordResetTokenRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.UserRepository;
import com.kapotiabi.security.JwtTokenProvider;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RepairShopRepository repairShopRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (req.getRole() == UserRole.ADMIN) {
      throw new IllegalArgumentException("Cannot self-register as admin");
    }
    if (userRepository.existsByEmail(req.getEmail())) {
      throw new DuplicateEmailException();
    }

    var user =
        UserEntity.builder()
            .email(req.getEmail())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .role(req.getRole())
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .phone(req.getPhone())
            .build();
    userRepository.save(user);

    String shopStatus = null;
    if (req.getRole() == UserRole.SHOP_OWNER) {
      var shop =
          RepairShopEntity.builder()
              .owner(user)
              .name(user.getFirstName() + " " + user.getLastName() + "'s Shop")
              .status("draft")
              .build();
      repairShopRepository.save(shop);
      shopStatus = "draft";
    }

    return buildAuthResponse(user, shopStatus);
  }

  public AuthResponse login(LoginRequest req) {
    var user =
        userRepository.findByEmail(req.getEmail()).orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    String shopStatus = null;
    if (user.getRole() == UserRole.SHOP_OWNER) {
      shopStatus =
          repairShopRepository
              .findByOwnerId(user.getId())
              .map(RepairShopEntity::getStatus)
              .orElse(null);
    }

    return buildAuthResponse(user, shopStatus);
  }

  @Transactional
  public Map<String, String> forgotPassword(ForgotPasswordRequest req) {
    userRepository
        .findByEmail(req.getEmail())
        .ifPresent(
            user -> {
              String token = UUID.randomUUID().toString();
              var resetToken =
                  PasswordResetTokenEntity.builder()
                      .user(user)
                      .token(token)
                      .expiresAt(LocalDateTime.now().plusHours(1))
                      .used(false)
                      .build();
              passwordResetTokenRepository.save(resetToken);
              log.info("Password reset requested for {}", req.getEmail());
            });
    return Map.of("message", "If that email exists, a reset link has been sent.");
  }

  @Transactional
  public Map<String, String> resetPassword(ResetPasswordRequest req) {
    var resetToken =
        passwordResetTokenRepository
            .findByToken(req.getToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid or unknown reset token"));

    if (resetToken.isUsed()) {
      throw new InvalidTokenException("Reset token has already been used");
    }
    if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException("Reset token has expired");
    }

    var user = resetToken.getUser();

    user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(user);

    resetToken.setUsed(true);
    passwordResetTokenRepository.save(resetToken);

    return Map.of("message", "Password updated successfully.");
  }

  @Transactional
  public void changePassword(Long userId, ChangePasswordRequest req) {
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));

    if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
      throw new InvalidCredentialsException("Current password is incorrect");
    }

    // TODO: reject if newPassword == currentPassword
    user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    userRepository.save(user);
  }

  private AuthResponse buildAuthResponse(UserEntity user, String shopStatus) {
    var token =
        jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().getDbValue());
    var expiresAt = jwtTokenProvider.getExpiry(token);
    return AuthResponse.builder()
        .token(token)
        .expiresAt(expiresAt)
        .role(user.getRole().getDbValue())
        .userId(user.getId())
        .shopStatus(shopStatus)
        .build();
  }
}
