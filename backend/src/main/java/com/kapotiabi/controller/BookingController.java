package com.kapotiabi.controller;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management")
public class BookingController {

  private final BookingService bookingService;
  private final ReviewService reviewService;

  @GetMapping
  @Operation(
      summary = "Get bookings for the authenticated user (car owner) or their shop (shop owner)")
  public ResponseEntity<List<BookingResponse>> getBookings() {
    Long authUserId = SecurityUtils.getAuthUserId();
    if (SecurityUtils.hasRole("SHOP_OWNER")) {
      return ResponseEntity.ok(bookingService.getByShopOwner(authUserId));
    }
    return ResponseEntity.ok(bookingService.getByUser(authUserId));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get booking by ID")
  public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.getById(id));
  }

  @PostMapping("/{id}/confirm")
  @Operation(summary = "Shop confirms a booking")
  public ResponseEntity<BookingResponse> confirm(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.confirm(id, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/schedule")
  @Operation(summary = "Schedule a booking")
  public ResponseEntity<BookingResponse> schedule(
      @PathVariable Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
    return ResponseEntity.ok(
        bookingService.schedule(id, scheduledAt, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/complete")
  @Operation(summary = "Mark a booking as completed")
  public ResponseEntity<BookingResponse> complete(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.complete(id, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel a booking")
  public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.cancel(id, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/review")
  @Operation(summary = "Submit a review for a completed booking")
  public ResponseEntity<ReviewResponse> submitReview(
      @PathVariable Long id, @Valid @RequestBody ReviewRequest req) {
    return ResponseEntity.ok(reviewService.submit(id, req, SecurityUtils.getAuthUserId()));
  }

  @GetMapping("/{id}/review")
  @Operation(summary = "Get the review for a booking")
  public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.getByBooking(id));
  }
}
