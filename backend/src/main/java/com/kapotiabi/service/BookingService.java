package com.kapotiabi.service;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final ShopOfferRepository shopOfferRepository;
  private final JobRequestRepository jobRequestRepository;

  public List<BookingResponse> getByUser(Long userId) {
    return bookingRepository.findByUserId(userId).stream().map(BookingResponse::from).toList();
  }

  public List<BookingResponse> getByShop(Long shopId) {
    return bookingRepository.findByShopId(shopId).stream().map(BookingResponse::from).toList();
  }

  public BookingResponse getById(Long id) {
    return BookingResponse.from(findOrThrow(id));
  }

  @Transactional
  public BookingResponse createFromOffer(Long offerId) {
    if (bookingRepository.findByOfferId(offerId).isPresent()) {
      throw new IllegalStateException("Booking already exists for offer: " + offerId);
    }
    var offer =
        shopOfferRepository
            .findById(offerId)
            .orElseThrow(() -> new NotFoundException("Shop offer not found: " + offerId));
    
    if (offer.getValidUntil() != null && offer.getValidUntil().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Offer has expired");
    }

    var jobRequest = offer.getJobRequest();
    
    offer.setStatus("accepted");
    shopOfferRepository.save(offer);

    jobRequest.setStatus("booked");
    jobRequest.setUpdatedAt(LocalDateTime.now());
    jobRequestRepository.save(jobRequest);

    var now = LocalDateTime.now();
    var booking =
        BookingEntity.builder()
            .offer(offer)
            .jobRequest(jobRequest)
            .user(jobRequest.getUser())
            .shop(offer.getShop())
            .confirmedByShop(false)
            .status("awaiting_shop")
            .createdAt(now)
            .updatedAt(now)
            .build();
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse confirm(Long id) {
    var booking = findOrThrow(id);
    booking.setConfirmedByShop(true);
    booking.setShopConfirmedAt(LocalDateTime.now());
    booking.setStatus("confirmed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse schedule(Long id, LocalDateTime scheduledAt) {
    var booking = findOrThrow(id);
    booking.setScheduledAt(scheduledAt);
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse complete(Long id) {
    var booking = findOrThrow(id);
    booking.setStatus("completed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse cancel(Long id) {
    var booking = findOrThrow(id);
    booking.setStatus("cancelled");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  private BookingEntity findOrThrow(Long id) {
    return bookingRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
  }
}
