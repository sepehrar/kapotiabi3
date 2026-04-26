package com.kapotiabi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kapotiabi.dto.response.PagedResponse;
import com.kapotiabi.dto.response.SavedShopResponse;
import com.kapotiabi.exception.AlreadySavedException;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.UserSavedShopService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserSavedShopControllerTest {

  @Mock private UserSavedShopService userSavedShopService;

  @InjectMocks private UserSavedShopController userSavedShopController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userSavedShopController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("5", null, List.of()));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private SavedShopResponse buildResponse() {
    return new SavedShopResponse(
        10L,
        "AutoExpert Tallinn",
        "Tallinn",
        "Harju",
        "+372 555 1234",
        "active",
        LocalDateTime.now());
  }

  private PagedResponse<SavedShopResponse> buildPagedResponse(List<SavedShopResponse> content) {
    return new PagedResponse<>(content, 0, 10, content.size(), 1);
  }

  // ── GET /api/v1/users/{userId}/saved-shops ────────────────────────────────

  @Test
  void getSaved_returns200WithPagedResult() throws Exception {
    when(userSavedShopService.getSaved(5L, 0, 10))
        .thenReturn(buildPagedResponse(List.of(buildResponse())));

    mockMvc
        .perform(get("/api/v1/users/me/saved-shops"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].shopId").value(10))
        .andExpect(jsonPath("$.content[0].name").value("AutoExpert Tallinn"))
        .andExpect(jsonPath("$.content[0].city").value("Tallinn"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void getSaved_customPageAndSize_passedToService() throws Exception {
    when(userSavedShopService.getSaved(5L, 1, 5))
        .thenReturn(new PagedResponse<>(List.of(), 1, 5, 0, 0));

    mockMvc
        .perform(get("/api/v1/users/me/saved-shops?page=1&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(5));
  }

  @Test
  void getSaved_emptyList_returns200() throws Exception {
    when(userSavedShopService.getSaved(5L, 0, 10)).thenReturn(buildPagedResponse(List.of()));

    mockMvc
        .perform(get("/api/v1/users/me/saved-shops"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  // ── POST /api/v1/users/{userId}/saved-shops/{shopId} ─────────────────────

  @Test
  void save_returns201WithBody() throws Exception {
    when(userSavedShopService.save(5L, 10L)).thenReturn(buildResponse());

    mockMvc
        .perform(post("/api/v1/users/me/saved-shops/10"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shopId").value(10))
        .andExpect(jsonPath("$.name").value("AutoExpert Tallinn"));
  }

  @Test
  void save_shopNotFound_returns404() throws Exception {
    when(userSavedShopService.save(5L, 99L)).thenThrow(new NotFoundException("Shop not found: 99"));

    mockMvc
        .perform(post("/api/v1/users/me/saved-shops/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Shop not found: 99"));
  }

  @Test
  void save_alreadySaved_returns409() throws Exception {
    when(userSavedShopService.save(5L, 10L)).thenThrow(new AlreadySavedException());

    mockMvc
        .perform(post("/api/v1/users/me/saved-shops/10"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Shop already saved"));
  }

  // ── DELETE /api/v1/users/{userId}/saved-shops/{shopId} ───────────────────

  @Test
  void unsave_returns204() throws Exception {
    doNothing().when(userSavedShopService).unsave(5L, 10L);

    mockMvc.perform(delete("/api/v1/users/me/saved-shops/10")).andExpect(status().isNoContent());
  }

  @Test
  void unsave_notFound_returns404() throws Exception {
    doThrow(new NotFoundException("Saved shop not found"))
        .when(userSavedShopService)
        .unsave(5L, 10L);

    mockMvc
        .perform(delete("/api/v1/users/me/saved-shops/10"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Saved shop not found"));
  }
}
