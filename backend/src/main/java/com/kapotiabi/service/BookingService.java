package com.kapotiabi.service;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.ShopOfferEntity;
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
  private final ShopOfferRepository offerRepository;
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

  // Called when a car owner accepts an offer
  @Transactional
  public BookingResponse createFromOffer(Long offerId) {
    if (bookingRepository.findByOfferId(offerId).isPresent()) {
      throw new IllegalStateException("Booking already exists for offer: " + offerId);
    }
    ShopOfferEntity offer =
        offerRepository
            .findById(offerId)
            .orElseThrow(() -> new NotFoundException("Offer not found: " + offerId));
    JobRequestEntity jr =
        jobRequestRepository
            .findById(offer.getJobRequestId())
            .orElseThrow(
                () -> new NotFoundException("Job request not found: " + offer.getJobRequestId()));

    offer.setStatus("accepted");
    offerRepository.save(offer);

    jr.setStatus("booked");
    jobRequestRepository.save(jr);

    BookingEntity booking = new BookingEntity();
    booking.setOfferId(offerId);
    booking.setJobRequestId(offer.getJobRequestId());
    booking.setUserId(jr.getUserId());
    booking.setShopId(offer.getShopId());
    booking.setConfirmedByShop(false);
    booking.setStatus("awaiting_shop");
    booking.setCreatedAt(LocalDateTime.now());
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse confirm(Long id) {
    BookingEntity booking = findOrThrow(id);
    booking.setConfirmedByShop(true);
    booking.setShopConfirmedAt(LocalDateTime.now());
    booking.setStatus("confirmed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse schedule(Long id, LocalDateTime scheduledAt) {
    BookingEntity booking = findOrThrow(id);
    booking.setScheduledAt(scheduledAt);
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse complete(Long id) {
    BookingEntity booking = findOrThrow(id);
    booking.setStatus("completed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  public BookingResponse cancel(Long id) {
    BookingEntity booking = findOrThrow(id);
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
