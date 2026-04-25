package com.kapotiabi.service;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.RepairShopRepository;
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
  private final RepairShopRepository repairShopRepository;

  public List<BookingResponse> getByUser(Long userId) {
    return bookingRepository.findByUserId(userId).stream().map(BookingResponse::from).toList();
  }

  public List<BookingResponse> getByShopOwner(Long ownerId) {
    RepairShopEntity shop =
        repairShopRepository
            .findByOwnerId(ownerId)
            .orElseThrow(() -> new NotFoundException("No shop found for user: " + ownerId));
    return bookingRepository.findByShopId(shop.getId()).stream()
        .map(BookingResponse::from)
        .toList();
  }

  public BookingResponse getById(Long id) {
    return BookingResponse.from(findOrThrow(id));
  }

  @Transactional
  public BookingResponse createFromOffer(Long offerId, Long authUserId) {
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
    if (!authUserId.equals(jobRequest.getUser().getId())) {
      throw new ForbiddenException("You don't have access to this job request");
    }

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

  @Transactional
  public BookingResponse confirm(Long id, Long authUserId) {
    var booking = findOrThrow(id);
    if (!"awaiting_shop".equals(booking.getStatus())) {
      throw new IllegalStateException(
          "Booking cannot be confirmed from status: " + booking.getStatus());
    }
    verifyShopOwner(authUserId, booking.getShop().getId());
    booking.setConfirmedByShop(true);
    booking.setShopConfirmedAt(LocalDateTime.now());
    booking.setStatus("confirmed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  @Transactional
  public BookingResponse schedule(Long id, LocalDateTime scheduledAt, Long authUserId) {
    var booking = findOrThrow(id);
    if (!"confirmed".equals(booking.getStatus())) {
      throw new IllegalStateException(
          "Booking cannot be scheduled from status: " + booking.getStatus());
    }
    verifyShopOwner(authUserId, booking.getShop().getId());
    booking.setScheduledAt(scheduledAt);
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  @Transactional
  public BookingResponse complete(Long id, Long authUserId) {
    var booking = findOrThrow(id);
    if (!"confirmed".equals(booking.getStatus())) {
      throw new IllegalStateException(
          "Booking cannot be completed from status: " + booking.getStatus());
    }
    verifyShopOwner(authUserId, booking.getShop().getId());
    booking.setStatus("completed");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  @Transactional
  public BookingResponse cancel(Long id, Long authUserId) {
    var booking = findOrThrow(id);
    if ("completed".equals(booking.getStatus()) || "cancelled".equals(booking.getStatus())) {
      throw new IllegalStateException(
          "Booking cannot be cancelled from status: " + booking.getStatus());
    }
    if (!authUserId.equals(booking.getUser().getId())) {
      boolean isShopOwner =
          repairShopRepository
              .findByOwnerId(authUserId)
              .map(shop -> shop.getId().equals(booking.getShop().getId()))
              .orElse(false);
      if (!isShopOwner) {
        throw new ForbiddenException("You don't have access to this booking");
      }
    }
    booking.setStatus("cancelled");
    booking.setUpdatedAt(LocalDateTime.now());
    return BookingResponse.from(bookingRepository.save(booking));
  }

  private void verifyShopOwner(Long authUserId, Long requiredShopId) {
    RepairShopEntity shop =
        repairShopRepository
            .findByOwnerId(authUserId)
            .orElseThrow(() -> new ForbiddenException("No shop found for this user"));
    if (!shop.getId().equals(requiredShopId)) {
      throw new ForbiddenException("You don't have access to this booking");
    }
  }

  private BookingEntity findOrThrow(Long id) {
    return bookingRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
  }
}
