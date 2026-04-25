package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.entity.*;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.RepairShopRepository;
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
  @Mock private RepairShopRepository repairShopRepository;

  @InjectMocks private BookingService bookingService;

  private static final Long CAR_OWNER_ID = 5L;
  private static final Long SHOP_OWNER_ID = 20L;
  private static final Long SHOP_ID = 3L;

  private BookingEntity buildBooking(String status) {
    return BookingEntity.builder()
        .id(1L)
        .offer(ShopOfferEntity.builder().id(2L).build())
        .jobRequest(JobRequestEntity.builder().id(10L).build())
        .user(UserEntity.builder().id(CAR_OWNER_ID).build())
        .shop(RepairShopEntity.builder().id(SHOP_ID).build())
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
        .shop(RepairShopEntity.builder().id(SHOP_ID).build())
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest() {
    return JobRequestEntity.builder()
        .id(10L)
        .user(UserEntity.builder().id(CAR_OWNER_ID).build())
        .car(CarEntity.builder().id(7L).build())
        .description("Oil change")
        .status("offers_received")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private RepairShopEntity buildShop() {
    return RepairShopEntity.builder()
        .id(SHOP_ID)
        .owner(UserEntity.builder().id(SHOP_OWNER_ID).build())
        .name("Test Shop")
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

    var response = bookingService.createFromOffer(2L, CAR_OWNER_ID);

    assertThat(response.offerId()).isEqualTo(2L);
    assertThat(response.status()).isEqualTo("awaiting_shop");
  }

  @Test
  void createFromOffer_alreadyExists() {
    when(bookingRepository.findByOfferId(2L))
        .thenReturn(Optional.of(buildBooking("awaiting_shop")));

    assertThatThrownBy(() -> bookingService.createFromOffer(2L, CAR_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Booking already exists for offer: 2");
  }

  @Test
  void createFromOffer_expiredOffer_throwsIllegalState() {
    ShopOfferEntity expiredOffer = buildOffer();
    expiredOffer.setValidUntil(LocalDateTime.now().minusDays(1));
    when(bookingRepository.findByOfferId(2L)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(2L)).thenReturn(Optional.of(expiredOffer));

    assertThatThrownBy(() -> bookingService.createFromOffer(2L, CAR_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Offer has expired");
  }

  @Test
  void createFromOffer_wrongUser_throwsForbidden() {
    when(bookingRepository.findByOfferId(2L)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(2L)).thenReturn(Optional.of(buildOffer()));

    assertThatThrownBy(() -> bookingService.createFromOffer(2L, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void confirm_success() {
    var booking = buildBooking("awaiting_shop");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.confirm(1L, SHOP_OWNER_ID);

    assertThat(response.status()).isEqualTo("confirmed");
    assertThat(response.confirmedByShop()).isTrue();
  }

  @Test
  void confirm_wrongStatus_throwsIllegalState() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));

    assertThatThrownBy(() -> bookingService.confirm(1L, SHOP_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cannot be confirmed");
  }

  @Test
  void confirm_wrongShop_throwsForbidden() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("awaiting_shop")));
    RepairShopEntity wrongShop =
        RepairShopEntity.builder()
            .id(99L)
            .owner(UserEntity.builder().id(SHOP_OWNER_ID).build())
            .build();
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(wrongShop));

    assertThatThrownBy(() -> bookingService.confirm(1L, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void schedule_success() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var scheduled = LocalDateTime.of(2026, 4, 12, 10, 0);
    var response = bookingService.schedule(1L, scheduled, SHOP_OWNER_ID);

    assertThat(response.scheduledAt()).isEqualTo(scheduled);
  }

  @Test
  void schedule_wrongStatus_throwsIllegalState() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("awaiting_shop")));

    assertThatThrownBy(() -> bookingService.schedule(1L, LocalDateTime.now(), SHOP_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cannot be scheduled");
  }

  @Test
  void complete_success() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.complete(1L, SHOP_OWNER_ID);

    assertThat(response.status()).isEqualTo("completed");
  }

  @Test
  void complete_wrongStatus_throwsIllegalState() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("awaiting_shop")));

    assertThatThrownBy(() -> bookingService.complete(1L, SHOP_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cannot be completed");
  }

  @Test
  void cancel_asCarOwner_success() {
    var booking = buildBooking("confirmed");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.cancel(1L, CAR_OWNER_ID);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void cancel_asShopOwner_success() {
    BookingEntity booking = buildBooking("awaiting_shop");
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = bookingService.cancel(1L, SHOP_OWNER_ID);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void cancel_alreadyCancelled_throwsIllegalState() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("cancelled")));

    assertThatThrownBy(() -> bookingService.cancel(1L, CAR_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cannot be cancelled");
  }

  @Test
  void cancel_unrelatedUser_throwsForbidden() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));
    when(repairShopRepository.findByOwnerId(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.cancel(1L, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void getById_notFound() {
    when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.getById(99L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void getByUser() {
    when(bookingRepository.findByUserId(CAR_OWNER_ID))
        .thenReturn(List.of(buildBooking("confirmed")));

    var result = bookingService.getByUser(CAR_OWNER_ID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().userId()).isEqualTo(CAR_OWNER_ID);
  }

  @Test
  void getByShopOwner() {
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.findByShopId(SHOP_ID)).thenReturn(List.of(buildBooking("confirmed")));

    var result = bookingService.getByShopOwner(SHOP_OWNER_ID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().shopId()).isEqualTo(SHOP_ID);
  }
}
