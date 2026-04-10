package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.ShopOfferService;
import java.math.BigDecimal;
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
class ShopOfferControllerTest {

  @Mock private ShopOfferService shopOfferService;
  @Mock private BookingService bookingService;

  @InjectMocks private ShopOfferController shopOfferController;

  private MockMvc mockMvc;

  private static final ShopOfferResponse OFFER =
      new ShopOfferResponse(
          1L,
          10L,
          2L,
          new BigDecimal("100.00"),
          "breakdown",
          "OEM",
          new BigDecimal("2.0"),
          null,
          null,
          "pending",
          LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(shopOfferController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(shopOfferService.getById(1L)).thenReturn(OFFER);

    mockMvc
        .perform(get("/api/v1/offers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("pending"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(shopOfferService.getById(99L))
        .thenThrow(new NotFoundException("Shop offer not found: 99"));

    mockMvc
        .perform(get("/api/v1/offers/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Shop offer not found: 99"));
  }

  @Test
  void update_validRequest_returns200() throws Exception {
    ShopOfferResponse updated =
        new ShopOfferResponse(
            1L,
            10L,
            2L,
            new BigDecimal("200.00"),
            "new breakdown",
            "OEM",
            null,
            null,
            null,
            "pending",
            LocalDateTime.now());
    when(shopOfferService.update(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/offers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"shopId":2,"priceEstimate":200.00,"breakdown":"new breakdown"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.priceEstimate").value(200.00));
  }

  @Test
  void accept_returns200() throws Exception {
    BookingResponse booking =
        new BookingResponse(
            1L, 1L, 10L, 5L, 2L, null, false, null, "awaiting_shop", LocalDateTime.now());
    when(bookingService.createFromOffer(1L)).thenReturn(booking);

    mockMvc
        .perform(post("/api/v1/offers/1/accept"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("awaiting_shop"));
  }

  @Test
  void accept_alreadyExists_returns409() throws Exception {
    when(bookingService.createFromOffer(1L))
        .thenThrow(new IllegalStateException("Booking already exists for offer: 1"));

    mockMvc
        .perform(post("/api/v1/offers/1/accept"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Booking already exists for offer: 1"));
  }

  @Test
  void reject_returns200() throws Exception {
    ShopOfferResponse rejected =
        new ShopOfferResponse(
            1L,
            10L,
            2L,
            new BigDecimal("100.00"),
            null,
            null,
            null,
            null,
            null,
            "rejected",
            LocalDateTime.now());
    when(shopOfferService.reject(1L)).thenReturn(rejected);

    mockMvc
        .perform(post("/api/v1/offers/1/reject"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("rejected"));
  }

  @Test
  void update_missingShopId_returns400() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/offers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"priceEstimate":200.00}
                    """))
        .andExpect(status().isBadRequest());
  }
}
