package com.kapotiabi.controller;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Bookings created when a car owner accepts an offer")
public class BookingController {

  private final BookingService bookingService;
  private final ReviewService reviewService;

  @Operation(summary = "List bookings by user or shop")
  @GetMapping
  public ResponseEntity<List<BookingResponse>> list(
      @RequestParam(required = false) Long userId, @RequestParam(required = false) Long shopId) {
    if (shopId != null) return ResponseEntity.ok(bookingService.getByShop(shopId));
    if (userId != null) return ResponseEntity.ok(bookingService.getByUser(userId));
    return ResponseEntity.badRequest().build();
  }

  @Operation(summary = "Get booking by ID")
  @GetMapping("/{id}")
  public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.getById(id));
  }

  @Operation(summary = "Shop confirms the booking")
  @PostMapping("/{id}/confirm")
  public ResponseEntity<BookingResponse> confirm(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.confirm(id));
  }

  @Operation(summary = "Set the scheduled appointment time")
  @PostMapping("/{id}/schedule")
  public ResponseEntity<BookingResponse> schedule(
      @PathVariable Long id, @RequestParam LocalDateTime scheduledAt) {
    return ResponseEntity.ok(bookingService.schedule(id, scheduledAt));
  }

  @Operation(summary = "Mark booking as completed")
  @PostMapping("/{id}/complete")
  public ResponseEntity<BookingResponse> complete(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.complete(id));
  }

  @Operation(summary = "Cancel a booking")
  @PostMapping("/{id}/cancel")
  public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.cancel(id));
  }

  @Operation(summary = "Submit a review for a completed booking")
  @PostMapping("/{id}/review")
  public ResponseEntity<ReviewResponse> submitReview(
      @PathVariable Long id, @Valid @RequestBody ReviewRequest req) {
    return ResponseEntity.ok(reviewService.submit(id, req));
  }

  @Operation(summary = "Get the review for a booking")
  @GetMapping("/{id}/review")
  public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.getByBooking(id));
  }
}
