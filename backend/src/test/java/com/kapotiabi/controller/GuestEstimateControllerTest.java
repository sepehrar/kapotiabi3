package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.GuestEstimateService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
class GuestEstimateControllerTest {

  @Mock private GuestEstimateService guestEstimateService;

  @InjectMocks private GuestEstimateController guestEstimateController;

  private MockMvc mockMvc;

  private static final GuestEstimateResponse ESTIMATE =
      new GuestEstimateResponse(
          1L,
          "Oil change",
          "Tallinn",
          null,
          new BigDecimal("30.00"),
          new BigDecimal("60.00"),
          LocalDateTime.now(),
          List.of());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(guestEstimateController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    when(guestEstimateService.create(any())).thenReturn(ESTIMATE);

    mockMvc
        .perform(
            post("/api/v1/guest-estimates")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"description":"Oil change","plateNumber":"ABC123","city":"Tallinn"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("Oil change"));
  }

  @Test
  void create_missingDescription_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/guest-estimates")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"plateNumber":"ABC123","city":"Tallinn"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(guestEstimateService.getById(1L)).thenReturn(ESTIMATE);

    mockMvc
        .perform(get("/api/v1/guest-estimates/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.city").value("Tallinn"))
        .andExpect(jsonPath("$.priceFrom").value(30.00));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(guestEstimateService.getById(99L))
        .thenThrow(new NotFoundException("Guest estimate not found: 99"));

    mockMvc
        .perform(get("/api/v1/guest-estimates/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Guest estimate not found: 99"));
  }
}
