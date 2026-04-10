package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.ReviewService;
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
class BookingControllerTest {

  @Mock private BookingService bookingService;
  @Mock private ReviewService reviewService;

  @InjectMocks private BookingController bookingController;

  private MockMvc mockMvc;

  private static final BookingResponse BOOKING =
      new BookingResponse(
          1L,
          2L,
          10L,
          5L,
          3L,
          null,
          false,
          null,
          "awaiting_shop",
          LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(bookingController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getBookings_byUserId_returns200() throws Exception {
    when(bookingService.getByUser(5L)).thenReturn(List.of(BOOKING));

    mockMvc
        .perform(get("/api/v1/bookings").param("userId", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(5));
  }

  @Test
  void getBookings_byShopId_returns200() throws Exception {
    when(bookingService.getByShop(3L)).thenReturn(List.of(BOOKING));

    mockMvc
        .perform(get("/api/v1/bookings").param("shopId", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].shopId").value(3));
  }

  @Test
  void getBookings_noParam_returns400() throws Exception {
    mockMvc
        .perform(get("/api/v1/bookings"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(bookingService.getById(1L)).thenReturn(BOOKING);

    mockMvc
        .perform(get("/api/v1/bookings/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("awaiting_shop"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(bookingService.getById(99L)).thenThrow(new NotFoundException("Booking not found: 99"));

    mockMvc
        .perform(get("/api/v1/bookings/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void confirm_returns200() throws Exception {
    BookingResponse confirmed =
        new BookingResponse(1L, 2L, 10L, 5L, 3L, null, true, LocalDateTime.now(), "confirmed", LocalDateTime.now());
    when(bookingService.confirm(1L)).thenReturn(confirmed);

    mockMvc
        .perform(post("/api/v1/bookings/1/confirm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("confirmed"))
        .andExpect(jsonPath("$.confirmedByShop").value(true));
  }

  @Test
  void complete_returns200() throws Exception {
    BookingResponse completed =
        new BookingResponse(1L, 2L, 10L, 5L, 3L, null, true, null, "completed", LocalDateTime.now());
    when(bookingService.complete(1L)).thenReturn(completed);

    mockMvc
        .perform(post("/api/v1/bookings/1/complete"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("completed"));
  }

  @Test
  void cancel_returns200() throws Exception {
    BookingResponse cancelled =
        new BookingResponse(1L, 2L, 10L, 5L, 3L, null, false, null, "cancelled", LocalDateTime.now());
    when(bookingService.cancel(1L)).thenReturn(cancelled);

    mockMvc
        .perform(post("/api/v1/bookings/1/cancel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("cancelled"));
  }

  @Test
  void schedule_returns200() throws Exception {
    LocalDateTime scheduled = LocalDateTime.of(2026, 5, 1, 10, 0);
    BookingResponse scheduledBooking =
        new BookingResponse(1L, 2L, 10L, 5L, 3L, scheduled, false, null, "confirmed", LocalDateTime.now());
    when(bookingService.schedule(eq(1L), any())).thenReturn(scheduledBooking);

    mockMvc
        .perform(
            post("/api/v1/bookings/1/schedule")
                .param("scheduledAt", "2026-05-01T10:00:00"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.scheduledAt").exists());
  }

  @Test
  void submitReview_valid_returns200() throws Exception {
    ReviewResponse review =
        new ReviewResponse(100L, 1L, 5L, 3L, 5, "Great service!", LocalDateTime.now());
    when(reviewService.submit(eq(1L), any())).thenReturn(review);

    mockMvc
        .perform(
            post("/api/v1/bookings/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"rating":5,"comment":"Great service!"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rating").value(5));
  }

  @Test
  void submitReview_invalidRating_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/bookings/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"rating":6,"comment":"Too high"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getReview_returns200() throws Exception {
    ReviewResponse review =
        new ReviewResponse(100L, 1L, 5L, 3L, 4, "Good", LocalDateTime.now());
    when(reviewService.getByBooking(1L)).thenReturn(review);

    mockMvc
        .perform(get("/api/v1/bookings/1/review"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").value(1));
  }
}
