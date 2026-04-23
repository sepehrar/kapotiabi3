package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.entity.*;
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

  @Mock private ReviewRepository reviewRepository;

  @Mock private BookingRepository bookingRepository;

  @InjectMocks private ReviewService reviewService;

  private BookingEntity buildBooking(String status) {
    return BookingEntity.builder()
        .id(1L)
        .offer(ShopOfferEntity.builder().id(2L).build())
        .jobRequest(JobRequestEntity.builder().id(10L).build())
        .user(UserEntity.builder().id(5L).build())
        .shop(RepairShopEntity.builder().id(3L).build())
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ReviewEntity buildReview() {
    return ReviewEntity.builder()
        .id(100L)
        .booking(BookingEntity.builder().id(1L).build())
        .user(UserEntity.builder().id(5L).build())
        .shop(RepairShopEntity.builder().id(3L).build())
        .rating(5)
        .comment("Great service!")
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void submit_success() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("completed")));
    when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.empty());
    var saved = buildReview();
    when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(saved);

    var req = new ReviewRequest(5, "Great service!");
    var response = reviewService.submit(1L, req);

    assertThat(response.rating()).isEqualTo(5);
    assertThat(response.bookingId()).isEqualTo(1L);
  }

  @Test
  void submit_notCompleted() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));

    var req = new ReviewRequest(5, "Great!");
    assertThatThrownBy(() -> reviewService.submit(1L, req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Booking is not completed");
  }

  @Test
  void submit_alreadyExists() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("completed")));
    when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.of(buildReview()));

    var req = new ReviewRequest(4, "Good.");
    assertThatThrownBy(() -> reviewService.submit(1L, req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Review already exists for this booking");
  }

  @Test
  void getByBooking() {
    when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.of(buildReview()));

    var response = reviewService.getByBooking(1L);

    assertThat(response.bookingId()).isEqualTo(1L);
    assertThat(response.rating()).isEqualTo(5);
  }

  @Test
  void getByBooking_notFound() {
    when(reviewRepository.findByBookingId(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.getByBooking(99L)).isInstanceOf(NotFoundException.class);
  }
}
