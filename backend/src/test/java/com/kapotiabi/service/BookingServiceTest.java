package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.ShopOfferEntity;
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
        .offerId(2L)
        .jobRequestId(10L)
        .userId(5L)
        .shopId(3L)
        .confirmedByShop(false)
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ShopOfferEntity buildOffer() {
    return ShopOfferEntity.builder()
        .id(2L)
        .jobRequestId(10L)
        .shopId(3L)
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest() {
    return JobRequestEntity.builder()
        .id(10L)
        .userId(5L)
        .carId(7L)
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
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(jobRequestRepository.save(any(JobRequestEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    BookingEntity saved = buildBooking("awaiting_shop");
    when(bookingRepository.save(any(BookingEntity.class))).thenReturn(saved);

    BookingResponse response = bookingService.createFromOffer(2L);

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
    BookingEntity booking = buildBooking("awaiting_shop");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.confirm(1L);

    assertThat(response.status()).isEqualTo("confirmed");
    assertThat(response.confirmedByShop()).isTrue();
  }

  @Test
  void schedule() {
    BookingEntity booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    LocalDateTime scheduled = LocalDateTime.of(2026, 4, 12, 10, 0);
    BookingResponse response = bookingService.schedule(1L, scheduled);

    assertThat(response.scheduledAt()).isEqualTo(scheduled);
  }

  @Test
  void complete() {
    BookingEntity booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.complete(1L);

    assertThat(response.status()).isEqualTo("completed");
  }

  @Test
  void cancel() {
    BookingEntity booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.cancel(1L);

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

    List<BookingResponse> result = bookingService.getByUser(5L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).userId()).isEqualTo(5L);
  }

  @Test
  void getByShop() {
    when(bookingRepository.findByShopId(3L)).thenReturn(List.of(buildBooking("confirmed")));

    List<BookingResponse> result = bookingService.getByShop(3L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).shopId()).isEqualTo(3L);
  }

  @Test
  void createFromOffer_offerNotFound_throwsNotFoundException() {
    when(bookingRepository.findByOfferId(99L)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.createFromOffer(99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }
}
