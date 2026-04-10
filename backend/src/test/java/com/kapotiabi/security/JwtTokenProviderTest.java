package com.kapotiabi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kapotiabi.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    JwtConfig config = new JwtConfig();
    // Must be at least 32 bytes for HMAC-SHA256
    config.setSecret("test-secret-key-that-is-long-enough-for-hmac");
    config.setExpirationMs(3_600_000L); // 1 hour
    jwtTokenProvider = new JwtTokenProvider(config);
  }

  @Test
  void generateToken_returnsNonNullToken() {
    String token = jwtTokenProvider.generateToken(1L, "user@test.ee", "car_owner");

    assertThat(token).isNotNull().isNotEmpty();
  }

  @Test
  void generateToken_tokenHasThreeParts() {
    String token = jwtTokenProvider.generateToken(1L, "user@test.ee", "car_owner");

    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void parseToken_returnsCorrectSubject() {
    String token = jwtTokenProvider.generateToken(42L, "user@test.ee", "car_owner");

    Claims claims = jwtTokenProvider.parseToken(token);

    assertThat(claims.getSubject()).isEqualTo("42");
  }

  @Test
  void parseToken_returnsCorrectEmailClaim() {
    String token = jwtTokenProvider.generateToken(1L, "admin@kapotiabi.ee", "admin");

    Claims claims = jwtTokenProvider.parseToken(token);

    assertThat(claims.get("email", String.class)).isEqualTo("admin@kapotiabi.ee");
  }

  @Test
  void parseToken_returnsCorrectRoleClaim() {
    String token = jwtTokenProvider.generateToken(1L, "shop@test.ee", "shop_owner");

    Claims claims = jwtTokenProvider.parseToken(token);

    assertThat(claims.get("role", String.class)).isEqualTo("shop_owner");
  }

  @Test
  void getExpiry_returnsExpiryInFuture() {
    String token = jwtTokenProvider.generateToken(1L, "user@test.ee", "car_owner");

    Instant expiry = jwtTokenProvider.getExpiry(token);

    assertThat(expiry).isAfter(Instant.now());
  }

  @Test
  void getExpiry_respectsConfiguredExpiration() {
    String token = jwtTokenProvider.generateToken(1L, "user@test.ee", "car_owner");

    Instant expiry = jwtTokenProvider.getExpiry(token);
    Instant expectedExpiry = Instant.now().plusMillis(3_600_000L);

    // Allow 5 seconds tolerance
    assertThat(expiry).isBetween(expectedExpiry.minusSeconds(5), expectedExpiry.plusSeconds(5));
  }

  @Test
  void parseToken_invalidToken_throwsJwtException() {
    assertThatThrownBy(() -> jwtTokenProvider.parseToken("not.a.valid.jwt"))
        .isInstanceOf(JwtException.class);
  }

  @Test
  void parseToken_tamperedToken_throwsJwtException() {
    String token = jwtTokenProvider.generateToken(1L, "user@test.ee", "car_owner");
    String tampered = token + "tampered";

    assertThatThrownBy(() -> jwtTokenProvider.parseToken(tampered))
        .isInstanceOf(JwtException.class);
  }
}
