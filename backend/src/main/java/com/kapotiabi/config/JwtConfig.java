package com.kapotiabi.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

  /** Same 256+ bit default as application.yaml; also applied when JWT_SECRET is set but empty. */
  public static final String DEFAULT_LOCAL_DEV_SECRET =
      "dev-secret-key-min-256-bits-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

  private String secret;
  private long expirationMs;

  @PostConstruct
  void ensureSecret() {
    if (secret == null || secret.isBlank()) {
      secret = DEFAULT_LOCAL_DEV_SECRET;
    }
  }
}
