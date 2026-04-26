package com.kapotiabi.service;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.ReviewEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.ReviewRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final BookingRepository bookingRepository;

  public ReviewResponse submit(Long bookingId, ReviewRequest req, Long authUserId) {
    BookingEntity booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    if (!"completed".equals(booking.getStatus())) {
      throw new IllegalStateException("Booking is not completed");
    }
    if (!authUserId.equals(booking.getUser().getId())) {
      throw new ForbiddenException("You don't have access to this booking");
    }
    if (reviewRepository.findByBookingId(bookingId).isPresent()) {
      throw new IllegalStateException("Review already exists for this booking");
    }
    ReviewEntity review =
        ReviewEntity.builder()
            .booking(booking)
            .user(booking.getUser())
            .shop(booking.getShop())
            .rating(req.rating())
            .comment(req.comment())
            .createdAt(LocalDateTime.now())
            .build();
    return ReviewResponse.from(reviewRepository.save(review));
  }

  public ReviewResponse getByBooking(Long bookingId) {
    return reviewRepository
        .findByBookingId(bookingId)
        .map(ReviewResponse::from)
        .orElseThrow(() -> new NotFoundException("Review not found for booking: " + bookingId));
  }
}
