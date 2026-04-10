package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  private MockMvc mockMvc;

  private static final UserResponse USER_RESPONSE =
      new UserResponse(
          1L,
          "user@test.ee",
          "car_owner",
          "Mari",
          "Mets",
          "555-1234",
          LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(userService.getById(1L)).thenReturn(USER_RESPONSE);

    mockMvc
        .perform(get("/api/v1/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("user@test.ee"))
        .andExpect(jsonPath("$.role").value("car_owner"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(userService.getById(99L)).thenThrow(new NotFoundException("User not found: 99"));

    mockMvc
        .perform(get("/api/v1/users/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found: 99"));
  }

  @Test
  void update_validRequest_returns200() throws Exception {
    UserResponse updated =
        new UserResponse(
            1L,
            "user@test.ee",
            "car_owner",
            "Jane",
            "Smith",
            "555-9999",
            LocalDateTime.now());
    when(userService.update(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Jane","lastName":"Smith","phone":"555-9999"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"));
  }

  @Test
  void update_missingFirstName_returns400() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"lastName":"Smith","phone":"555-9999"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_notFound_returns404() throws Exception {
    when(userService.update(eq(99L), any())).thenThrow(new NotFoundException("User not found: 99"));

    mockMvc
        .perform(
            put("/api/v1/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Jane","lastName":"Smith","phone":"555-9999"}
                    """))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found: 99"));
  }
}
