package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

  @Mock UserRepository userRepository;
  @Mock RepairShopRepository repairShopRepository;
  @Mock PasswordResetTokenRepository passwordResetTokenRepository;
  @Mock JwtTokenProvider jwtTokenProvider;

  // Real encoder so BCrypt actually works in tests
  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  AuthService authService;

  @BeforeEach
  void setUp() {
    authService =
        new AuthService(
            userRepository,
            repairShopRepository,
            passwordResetTokenRepository,
            passwordEncoder,
            jwtTokenProvider);
    when(jwtTokenProvider.generateToken(any(), any(), any())).thenReturn("test.jwt.token");
    when(jwtTokenProvider.getExpiry(any()))
        .thenReturn(java.time.Instant.parse("2026-04-07T10:00:00Z"));
  }

  // ── Register ──────────────────────────────────────────────────────────────

  @Test
  void register_carOwner_savesUserNoShop() {
    when(userRepository.existsByEmail("user@test.ee")).thenReturn(false);
    when(userRepository.save(any()))
        .thenAnswer(
            inv -> {
              UserEntity u = inv.getArgument(0);
              u.setId(1L);
              return u;
            });

    RegisterRequest req = new RegisterRequest();
    req.setEmail("user@test.ee");
    req.setPassword("Test123");
    req.setFirstName("Mari");
    req.setLastName("Mets");
    req.setRole(UserRole.CAR_OWNER);

    AuthResponse response = authService.register(req);

    assertThat(response.getRole()).isEqualTo("car_owner");
    assertThat(response.getShopStatus()).isNull();
    verify(repairShopRepository, never()).save(any());
  }

  @Test
  void register_shopOwner_savesUserAndDraftShop() {
    when(userRepository.existsByEmail("shop@test.ee")).thenReturn(false);
    when(userRepository.save(any()))
        .thenAnswer(
            inv -> {
              UserEntity u = inv.getArgument(0);
              u.setId(2L);
              return u;
            });

    RegisterRequest req = new RegisterRequest();
    req.setEmail("shop@test.ee");
    req.setPassword("Test123");
    req.setFirstName("Jaan");
    req.setLastName("Tamm");
    req.setRole(UserRole.SHOP_OWNER);

    AuthResponse response = authService.register(req);

    assertThat(response.getRole()).isEqualTo("shop_owner");
    assertThat(response.getShopStatus()).isEqualTo("draft");

    ArgumentCaptor<RepairShopEntity> shopCaptor = ArgumentCaptor.forClass(RepairShopEntity.class);
    verify(repairShopRepository).save(shopCaptor.capture());
    assertThat(shopCaptor.getValue().getStatus()).isEqualTo("draft");
    assertThat(shopCaptor.getValue().getOwner().getId()).isEqualTo(2L);
  }

  @Test
  void register_adminRole_throwsIllegalArgument() {
    RegisterRequest req = new RegisterRequest();
    req.setEmail("admin@test.ee");
    req.setPassword("Test123");
    req.setFirstName("Bad");
    req.setLastName("Actor");
    req.setRole(UserRole.ADMIN);

    assertThatThrownBy(() -> authService.register(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cannot self-register as admin");

    verify(userRepository, never()).save(any());
  }

  @Test
  void register_duplicateEmail_throwsDuplicateEmailException() {
    when(userRepository.existsByEmail("user@test.ee")).thenReturn(true);

    RegisterRequest req = new RegisterRequest();
    req.setEmail("user@test.ee");
    req.setPassword("Test123");
    req.setFirstName("Mari");
    req.setLastName("Mets");
    req.setRole(UserRole.CAR_OWNER);

    assertThatThrownBy(() -> authService.register(req)).isInstanceOf(DuplicateEmailException.class);

    verify(userRepository, never()).save(any());
  }

  @Test
  void register_passwordIsHashed() {
    when(userRepository.existsByEmail(any())).thenReturn(false);
    when(userRepository.save(any()))
        .thenAnswer(
            inv -> {
              UserEntity u = inv.getArgument(0);
              u.setId(1L);
              return u;
            });

    RegisterRequest req = new RegisterRequest();
    req.setEmail("user@test.ee");
    req.setPassword("PlainText123");
    req.setFirstName("Mari");
    req.setLastName("Mets");
    req.setRole(UserRole.CAR_OWNER);

    authService.register(req);

    ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getPasswordHash()).isNotEqualTo("PlainText123");
    assertThat(passwordEncoder.matches("PlainText123", userCaptor.getValue().getPasswordHash()))
        .isTrue();
  }

  // ── Login ─────────────────────────────────────────────────────────────────

  @Test
  void login_validCredentials_returnsAuthResponse() {
    String hash = passwordEncoder.encode("Test123");
    UserEntity user =
        UserEntity.builder()
            .id(1L)
            .email("user@test.ee")
            .passwordHash(hash)
            .role(UserRole.CAR_OWNER)
            .build();
    when(userRepository.findByEmail("user@test.ee")).thenReturn(Optional.of(user));

    LoginRequest req = new LoginRequest();
    req.setEmail("user@test.ee");
    req.setPassword("Test123");

    AuthResponse response = authService.login(req);

    assertThat(response.getToken()).isEqualTo("test.jwt.token");
    assertThat(response.getRole()).isEqualTo("car_owner");
    assertThat(response.getUserId()).isEqualTo(1L);
  }

  @Test
  void login_adminRole_returnsAuthResponseWithAdminRole() {
    String hash = passwordEncoder.encode("Admin1234");
    UserEntity admin =
        UserEntity.builder()
            .id(1L)
            .email("admin@kapotiabi.ee")
            .passwordHash(hash)
            .role(UserRole.ADMIN)
            .build();
    when(userRepository.findByEmail("admin@kapotiabi.ee")).thenReturn(Optional.of(admin));

    LoginRequest req = new LoginRequest();
    req.setEmail("admin@kapotiabi.ee");
    req.setPassword("Admin1234");

    AuthResponse response = authService.login(req);

    assertThat(response.getRole()).isEqualTo("admin");
    assertThat(response.getShopStatus()).isNull();
  }

  @Test
  void login_shopOwner_includesShopStatus() {
    String hash = passwordEncoder.encode("Test123");
    UserEntity user =
        UserEntity.builder()
            .id(2L)
            .email("shop@test.ee")
            .passwordHash(hash)
            .role(UserRole.SHOP_OWNER)
            .build();
    when(userRepository.findByEmail("shop@test.ee")).thenReturn(Optional.of(user));
    when(repairShopRepository.findByOwnerId(2L))
        .thenReturn(Optional.of(RepairShopEntity.builder().status("draft").build()));

    LoginRequest req = new LoginRequest();
    req.setEmail("shop@test.ee");
    req.setPassword("Test123");

    AuthResponse response = authService.login(req);

    assertThat(response.getShopStatus()).isEqualTo("draft");
  }

  @Test
  void login_wrongPassword_throwsInvalidCredentials() {
    String hash = passwordEncoder.encode("Test123");
    UserEntity user =
        UserEntity.builder()
            .id(1L)
            .email("user@test.ee")
            .passwordHash(hash)
            .role(UserRole.CAR_OWNER)
            .build();
    when(userRepository.findByEmail("user@test.ee")).thenReturn(Optional.of(user));

    LoginRequest req = new LoginRequest();
    req.setEmail("user@test.ee");
    req.setPassword("WrongPass");

    assertThatThrownBy(() -> authService.login(req))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void login_unknownEmail_throwsInvalidCredentials() {
    when(userRepository.findByEmail("nobody@test.ee")).thenReturn(Optional.empty());

    LoginRequest req = new LoginRequest();
    req.setEmail("nobody@test.ee");
    req.setPassword("Test123");

    assertThatThrownBy(() -> authService.login(req))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  // ── Forgot password ───────────────────────────────────────────────────────

  @Test
  void forgotPassword_existingEmail_savesToken() {
    UserEntity user = UserEntity.builder().id(1L).email("user@test.ee").build();
    when(userRepository.findByEmail("user@test.ee")).thenReturn(Optional.of(user));

    ForgotPasswordRequest req = new ForgotPasswordRequest();
    req.setEmail("user@test.ee");

    authService.forgotPassword(req);

    ArgumentCaptor<PasswordResetTokenEntity> tokenCaptor =
        ArgumentCaptor.forClass(PasswordResetTokenEntity.class);
    verify(passwordResetTokenRepository).save(tokenCaptor.capture());
    assertThat(tokenCaptor.getValue().getUser().getId()).isEqualTo(1L);
    assertThat(tokenCaptor.getValue().isUsed()).isFalse();
    assertThat(tokenCaptor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());
  }

  @Test
  void forgotPassword_unknownEmail_noTokenSaved() {
    when(userRepository.findByEmail("nobody@test.ee")).thenReturn(Optional.empty());

    ForgotPasswordRequest req = new ForgotPasswordRequest();
    req.setEmail("nobody@test.ee");

    authService.forgotPassword(req);

    verify(passwordResetTokenRepository, never()).save(any());
  }

  // ── Reset password ────────────────────────────────────────────────────────

  @Test
  void resetPassword_validToken_updatesPasswordAndMarksUsed() {
    UserEntity user =
        UserEntity.builder()
            .id(1L)
            .email("user@test.ee")
            .passwordHash("oldhash")
            .role(UserRole.CAR_OWNER)
            .build();
    PasswordResetTokenEntity token =
        PasswordResetTokenEntity.builder()
            .id(1L)
            .user(user)
            .token("valid-uuid")
            .expiresAt(LocalDateTime.now().plusHours(1))
            .used(false)
            .build();
    when(passwordResetTokenRepository.findByToken("valid-uuid")).thenReturn(Optional.of(token));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setToken("valid-uuid");
    req.setNewPassword("NewPass456");

    authService.resetPassword(req);

    assertThat(token.isUsed()).isTrue();
    assertThat(passwordEncoder.matches("NewPass456", user.getPasswordHash())).isTrue();
  }

  @Test
  void resetPassword_expiredToken_throwsInvalidToken() {
    PasswordResetTokenEntity token =
        PasswordResetTokenEntity.builder()
            .id(1L)
            .user(UserEntity.builder().id(1L).build())
            .token("expired-uuid")
            .expiresAt(LocalDateTime.now().minusHours(1)) // already expired
            .used(false)
            .build();
    when(passwordResetTokenRepository.findByToken("expired-uuid")).thenReturn(Optional.of(token));

    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setToken("expired-uuid");
    req.setNewPassword("NewPass456");

    assertThatThrownBy(() -> authService.resetPassword(req))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage("Reset token has expired");
  }

  @Test
  void resetPassword_usedToken_throwsInvalidToken() {
    PasswordResetTokenEntity token =
        PasswordResetTokenEntity.builder()
            .id(1L)
            .user(UserEntity.builder().id(1L).build())
            .token("used-uuid")
            .expiresAt(LocalDateTime.now().plusHours(1))
            .used(true) // already used
            .build();
    when(passwordResetTokenRepository.findByToken("used-uuid")).thenReturn(Optional.of(token));

    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setToken("used-uuid");
    req.setNewPassword("NewPass456");

    assertThatThrownBy(() -> authService.resetPassword(req))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage("Reset token has already been used");
  }

  // ── Change password ───────────────────────────────────────────────────────

  @Test
  void changePassword_success_updatesHash() {
    String oldHash = passwordEncoder.encode("OldPass123");
    UserEntity user =
        UserEntity.builder()
            .id(1L)
            .email("user@test.ee")
            .passwordHash(oldHash)
            .role(UserRole.CAR_OWNER)
            .build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    authService.changePassword(1L, new ChangePasswordRequest("OldPass123", "NewPass456"));

    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(captor.capture());
    assertThat(passwordEncoder.matches("NewPass456", captor.getValue().getPasswordHash())).isTrue();
  }

  @Test
  void changePassword_wrongCurrentPassword_throwsInvalidCredentials() {
    String hash = passwordEncoder.encode("CorrectPass");
    UserEntity user =
        UserEntity.builder()
            .id(1L)
            .email("user@test.ee")
            .passwordHash(hash)
            .role(UserRole.CAR_OWNER)
            .build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThatThrownBy(
            () ->
                authService.changePassword(
                    1L, new ChangePasswordRequest("WrongPass", "NewPass456")))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessage("Current password is incorrect");

    verify(userRepository, never()).save(any());
  }

  @Test
  void changePassword_userNotFound_throwsNotFoundException() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                authService.changePassword(99L, new ChangePasswordRequest("OldPass", "NewPass456")))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void resetPassword_unknownToken_throwsInvalidToken() {
    when(passwordResetTokenRepository.findByToken("unknown-uuid")).thenReturn(Optional.empty());

    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setToken("unknown-uuid");
    req.setNewPassword("NewPass456");

    assertThatThrownBy(() -> authService.resetPassword(req))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage("Invalid or unknown reset token");
  }
}
