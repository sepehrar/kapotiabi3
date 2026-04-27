package com.kapotiabi.security;

import com.kapotiabi.config.JwtConfig;
import com.kapotiabi.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtConfig jwtConfig;

  @PostConstruct
  void requireSigningKey() {
    try {
      signingKey();
    } catch (WeakKeyException e) {
      throw new IllegalStateException(
          "JWT secret is too short for HMAC-SHA. Use at least 256 bits (e.g. set JWT_SECRET in the environment, or rely on the local-dev default in application.yaml).",
          e);
    }
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Long userId, String email, String role) {
    Instant now = Instant.now();
    Instant expiry = now.plusMillis(jwtConfig.getExpirationMs());
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim(SecurityConstants.EMAIL_CLAIM, email)
        .claim(SecurityConstants.ROLE_CLAIM, role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .signWith(signingKey())
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
  }

  public Instant getExpiry(String token) {
    return parseToken(token).getExpiration().toInstant();
  }
}
