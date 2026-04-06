package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.ReviewEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock ReviewRepository reviewRepository;
  @Mock BookingRepository bookingRepository;
  @InjectMocks ReviewService reviewService;

  private BookingEntity completedBooking() {
    BookingEntity b = new BookingEntity();
    b.setId(20L);
    b.setUserId(2L);
    b.setShopId(5L);
    b.setStatus("completed");
    return b;
  }

  @Test
  void submit_success() {
    when(reviewRepository.findByBookingId(20L)).thenReturn(Optional.empty());
    when(bookingRepository.findById(20L)).thenReturn(Optional.of(completedBooking()));
    when(reviewRepository.save(any()))
        .thenAnswer(
            inv -> {
              ReviewEntity r = inv.getArgument(0);
              r.setId(1L);
              r.setCreatedAt(LocalDateTime.now());
              return r;
            });

    var req = new ReviewRequest(5, "Great service!");
    ReviewResponse result = reviewService.submit(20L, req);

    assertThat(result.rating()).isEqualTo(5);
    assertThat(result.comment()).isEqualTo("Great service!");
    assertThat(result.shopId()).isEqualTo(5L);
  }

  @Test
  void submit_alreadyExists_throws() {
    ReviewEntity existing = new ReviewEntity();
    when(reviewRepository.findByBookingId(20L)).thenReturn(Optional.of(existing));

    assertThatThrownBy(() -> reviewService.submit(20L, new ReviewRequest(4, "ok")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void submit_bookingNotCompleted_throws() {
    BookingEntity b = completedBooking();
    b.setStatus("confirmed");
    when(reviewRepository.findByBookingId(20L)).thenReturn(Optional.empty());
    when(bookingRepository.findById(20L)).thenReturn(Optional.of(b));

    assertThatThrownBy(() -> reviewService.submit(20L, new ReviewRequest(3, "meh")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("completed");
  }

  @Test
  void submit_bookingNotFound_throws() {
    when(reviewRepository.findByBookingId(99L)).thenReturn(Optional.empty());
    when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.submit(99L, new ReviewRequest(3, "test")))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getByBooking_success() {
    ReviewEntity review = new ReviewEntity();
    review.setId(1L);
    review.setBookingId(20L);
    review.setUserId(2L);
    review.setShopId(5L);
    review.setRating(5);
    review.setCreatedAt(LocalDateTime.now());
    when(reviewRepository.findByBookingId(20L)).thenReturn(Optional.of(review));

    ReviewResponse result = reviewService.getByBooking(20L);

    assertThat(result.rating()).isEqualTo(5);
    assertThat(result.bookingId()).isEqualTo(20L);
  }

  @Test
  void getByBooking_notFound_throws() {
    when(reviewRepository.findByBookingId(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.getByBooking(99L)).isInstanceOf(NotFoundException.class);
  }
}
