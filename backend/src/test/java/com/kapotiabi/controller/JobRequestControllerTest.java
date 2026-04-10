package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.JobRequestService;
import com.kapotiabi.service.ShopOfferService;
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
class JobRequestControllerTest {

  @Mock private JobRequestService jobRequestService;
  @Mock private ShopOfferService shopOfferService;

  @InjectMocks private JobRequestController jobRequestController;

  private MockMvc mockMvc;

  private static final JobRequestResponse JOB_REQUEST =
      new JobRequestResponse(
          1L,
          5L,
          3L,
          "Oil change",
          false,
          "OEM",
          "city",
          "Tallinn",
          "Harju",
          null,
          "pending",
          null,
          LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(jobRequestController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getByUser_returns200() throws Exception {
    when(jobRequestService.getByUser(5L)).thenReturn(List.of(JOB_REQUEST));

    mockMvc
        .perform(get("/api/v1/job-requests").param("userId", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(5));
  }

  @Test
  void getByUser_noParam_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/job-requests")).andExpect(status().isBadRequest());
  }

  @Test
  void getAvailable_returns200() throws Exception {
    when(jobRequestService.getAvailable("Tallinn", null)).thenReturn(List.of(JOB_REQUEST));

    mockMvc
        .perform(get("/api/v1/job-requests/available").param("city", "Tallinn"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].city").value("Tallinn"));
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(jobRequestService.getById(1L)).thenReturn(JOB_REQUEST);

    mockMvc
        .perform(get("/api/v1/job-requests/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("pending"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(jobRequestService.getById(99L))
        .thenThrow(new NotFoundException("Job request not found: 99"));

    mockMvc.perform(get("/api/v1/job-requests/99")).andExpect(status().isNotFound());
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    when(jobRequestService.create(any())).thenReturn(JOB_REQUEST);

    mockMvc
        .perform(
            post("/api/v1/job-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"userId":5,"carId":3,"description":"Oil change"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(5));
  }

  @Test
  void create_missingDescription_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/job-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"userId":5,"carId":3}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_validRequest_returns200() throws Exception {
    JobRequestResponse updated =
        new JobRequestResponse(
            1L,
            5L,
            3L,
            "Brake check",
            true,
            "OEM",
            "city",
            "Tartu",
            null,
            null,
            "pending",
            null,
            LocalDateTime.now());
    when(jobRequestService.update(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/job-requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"userId":5,"carId":3,"description":"Brake check"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Brake check"));
  }

  @Test
  void cancel_returns200() throws Exception {
    JobRequestResponse cancelled =
        new JobRequestResponse(
            1L,
            5L,
            3L,
            "Oil change",
            false,
            "OEM",
            "city",
            "Tallinn",
            "Harju",
            null,
            "cancelled",
            null,
            LocalDateTime.now());
    when(jobRequestService.cancel(1L)).thenReturn(cancelled);

    mockMvc
        .perform(delete("/api/v1/job-requests/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("cancelled"));
  }

  @Test
  void getOffers_returns200() throws Exception {
    ShopOfferResponse offer =
        new ShopOfferResponse(
            1L,
            1L,
            2L,
            new BigDecimal("100.00"),
            null,
            null,
            null,
            null,
            null,
            "pending",
            LocalDateTime.now());
    when(shopOfferService.getByJobRequest(1L)).thenReturn(List.of(offer));

    mockMvc
        .perform(get("/api/v1/job-requests/1/offers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].shopId").value(2));
  }

  @Test
  void createOffer_validRequest_returns201() throws Exception {
    ShopOfferResponse offer =
        new ShopOfferResponse(
            1L,
            1L,
            2L,
            new BigDecimal("100.00"),
            null,
            "OEM",
            null,
            null,
            null,
            "pending",
            LocalDateTime.now());
    when(shopOfferService.create(eq(1L), any())).thenReturn(offer);

    mockMvc
        .perform(
            post("/api/v1/job-requests/1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"shopId":2,"priceEstimate":100.00}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shopId").value(2));
  }
}
