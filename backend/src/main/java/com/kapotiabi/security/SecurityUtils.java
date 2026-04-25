package com.kapotiabi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  private SecurityUtils() {}

  public static Long getAuthUserId() {
    return Long.parseLong(
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  public static boolean hasRole(String role) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.toUpperCase()));
  }
}
