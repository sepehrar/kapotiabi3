package com.kapotiabi.security;

import com.kapotiabi.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
    if (header != null && header.startsWith(SecurityConstants.BEARER_PREFIX)) {
      String token = header.substring(SecurityConstants.BEARER_PREFIX.length());
      try {
        Claims claims = jwtTokenProvider.parseToken(token);
        String role = claims.get(SecurityConstants.ROLE_CLAIM, String.class);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                List.of(
                    new SimpleGrantedAuthority(
                        SecurityConstants.ROLE_AUTHORITY_PREFIX + role.toUpperCase())));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (JwtException ex) {
        log.warn(
            "Ignoring invalid JWT for {} {}: {}",
            request.getMethod(),
            request.getRequestURI(),
            ex.getMessage());
      }
    }
    filterChain.doFilter(request, response);
  }
}
