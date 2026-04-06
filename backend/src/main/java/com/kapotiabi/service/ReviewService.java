package com.kapotiabi.service;

import com.kapotiabi.dto.request.ReviewRequest;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.ReviewEntity;
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

  public ReviewResponse submit(Long bookingId, ReviewRequest req) {
    if (reviewRepository.findByBookingId(bookingId).isPresent()) {
      throw new IllegalStateException("Review already exists for booking: " + bookingId);
    }
    BookingEntity booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    if (!"completed".equals(booking.getStatus())) {
      throw new IllegalArgumentException("Can only review completed bookings");
    }
    ReviewEntity review = new ReviewEntity();
    review.setBookingId(bookingId);
    review.setUserId(booking.getUserId());
    review.setShopId(booking.getShopId());
    review.setRating(req.rating());
    review.setComment(req.comment());
    review.setCreatedAt(LocalDateTime.now());
    return ReviewResponse.from(reviewRepository.save(review));
  }

  public ReviewResponse getByBooking(Long bookingId) {
    return ReviewResponse.from(
        reviewRepository
            .findByBookingId(bookingId)
            .orElseThrow(
                () -> new NotFoundException("Review not found for booking: " + bookingId)));
  }
}
