package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.entity.*;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;

  @Mock private ShopOfferRepository shopOfferRepository;

  @Mock private JobRequestRepository jobRequestRepository;

  @InjectMocks private BookingService bookingService;

  private BookingEntity buildBooking(String status) {
    return BookingEntity.builder()
        .id(1L)
        .offer(ShopOfferEntity.builder().id(2L).build())
        .jobRequest(JobRequestEntity.builder().id(10L).build())
        .user(UserEntity.builder().id(5L).build())
        .shop(RepairShopEntity.builder().id(3L).build())
        .confirmedByShop(false)
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ShopOfferEntity buildOffer() {
    return ShopOfferEntity.builder()
        .id(2L)
        .jobRequest(buildJobRequest())
        .shop(RepairShopEntity.builder().id(3L).build())
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest() {
    return JobRequestEntity.builder()
        .id(10L)
        .user(UserEntity.builder().id(5L).build())
        .car(CarEntity.builder().id(7L).build())
        .description("Oil change")
        .status("offers_received")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  void createFromOffer_success() {
    when(bookingRepository.findByOfferId(2L)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(2L)).thenReturn(Optional.of(buildOffer()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(jobRequestRepository.save(any(JobRequestEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    var saved = buildBooking("awaiting_shop");
    when(bookingRepository.save(any(BookingEntity.class))).thenReturn(saved);

    var response = bookingService.createFromOffer(2L);

    assertThat(response.offerId()).isEqualTo(2L);
    assertThat(response.status()).isEqualTo("awaiting_shop");
  }

  @Test
  void createFromOffer_alreadyExists() {
    when(bookingRepository.findByOfferId(2L))
        .thenReturn(Optional.of(buildBooking("awaiting_shop")));

    assertThatThrownBy(() -> bookingService.createFromOffer(2L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Booking already exists for offer: 2");
  }

  @Test
  void confirm() {
    var booking = buildBooking("awaiting_shop");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.confirm(1L);

    assertThat(response.status()).isEqualTo("confirmed");
    assertThat(response.confirmedByShop()).isTrue();
  }

  @Test
  void schedule() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var scheduled = LocalDateTime.of(2026, 4, 12, 10, 0);
    var response = bookingService.schedule(1L, scheduled);

    assertThat(response.scheduledAt()).isEqualTo(scheduled);
  }

  @Test
  void complete() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.complete(1L);

    assertThat(response.status()).isEqualTo("completed");
  }

  @Test
  void cancel() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.cancel(1L);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void getById_notFound() {
    when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.getById(99L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void getByUser() {
    when(bookingRepository.findByUserId(5L)).thenReturn(List.of(buildBooking("confirmed")));

    var result = bookingService.getByUser(5L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().userId()).isEqualTo(5L);
  }
}
