package com.kapotiabi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

  private JwtTokenProvider jwtTokenProvider;
  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = mock(JwtTokenProvider.class);
    filter = new JwtAuthenticationFilter(jwtTokenProvider);
    SecurityContextHolder.clearContext();
  }

  @Test
  void validBearerToken_setsAuthenticationInContext() throws Exception {
    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("1");
    when(claims.get("role", String.class)).thenReturn("car_owner");
    when(jwtTokenProvider.parseToken("valid.jwt.token")).thenReturn(claims);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer valid.jwt.token");
    HttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("1");
    assertThat(
            SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList())
        .containsExactly("ROLE_CAR_OWNER");
  }

  @Test
  void invalidBearerToken_continuesUnauthenticated() throws Exception {
    when(jwtTokenProvider.parseToken("bad.jwt.token")).thenThrow(new JwtException("invalid"));

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer bad.jwt.token");
    HttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void noAuthorizationHeader_continuesUnauthenticated() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void nonBearerHeader_continuesUnauthenticated() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
    HttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void validBearerToken_filterChainContinues() throws Exception {
    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("1");
    when(claims.get("role", String.class)).thenReturn("car_owner");
    when(jwtTokenProvider.parseToken("valid.jwt.token")).thenReturn(claims);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer valid.jwt.token");
    HttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    org.mockito.Mockito.verify(chain).doFilter(request, response);
  }
}
