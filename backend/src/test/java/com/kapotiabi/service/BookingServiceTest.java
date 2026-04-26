package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.BookingEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.OfferSlotEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopOfferEntity;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;
  @Mock private ShopOfferRepository shopOfferRepository;
  @Mock private JobRequestRepository jobRequestRepository;
  @Mock private RepairShopRepository repairShopRepository;
  @Mock private ShopOfferService shopOfferService;

  @InjectMocks private BookingService bookingService;

  private static final Long CAR_OWNER_ID = 5L;
  private static final Long SHOP_OWNER_ID = 20L;
  private static final Long SHOP_ID = 3L;
  private static final Long OFFER_ID = 2L;
  private static final Long SLOT_ID = 7L;

  private BookingEntity buildBooking(String status) {
    return BookingEntity.builder()
        .id(1L)
        .offerId(OFFER_ID)
        .jobRequestId(10L)
        .userId(CAR_OWNER_ID)
        .shopId(SHOP_ID)
        .scheduledAt(LocalDateTime.of(2026, 5, 10, 10, 0))
        .confirmedByShop(false)
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ShopOfferEntity buildOffer() {
    return ShopOfferEntity.builder()
        .id(OFFER_ID)
        .jobRequestId(10L)
        .shopId(SHOP_ID)
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest() {
    return JobRequestEntity.builder()
        .id(10L)
        .userId(CAR_OWNER_ID)
        .carId(7L)
        .description("Oil change")
        .status("offers_received")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private RepairShopEntity buildShop() {
    return RepairShopEntity.builder().id(SHOP_ID).ownerId(SHOP_OWNER_ID).name("Test Shop").build();
  }

  private OfferSlotEntity buildSlot(Long offerId) {
    return OfferSlotEntity.builder()
        .id(SLOT_ID)
        .offerId(offerId)
        .startsAt(LocalDateTime.of(2026, 5, 10, 10, 0))
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void createFromOffer_success() {
    when(bookingRepository.findByOfferId(OFFER_ID)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(OFFER_ID)).thenReturn(Optional.of(buildOffer()));
    when(shopOfferService.getSlotOrThrow(SLOT_ID)).thenReturn(buildSlot(OFFER_ID));
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(jobRequestRepository.save(any(JobRequestEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(bookingRepository.save(any(BookingEntity.class)))
        .thenReturn(buildBooking("awaiting_shop"));

    BookingResponse response = bookingService.createFromOffer(OFFER_ID, SLOT_ID, CAR_OWNER_ID);

    assertThat(response.offerId()).isEqualTo(OFFER_ID);
    assertThat(response.status()).isEqualTo("awaiting_shop");
    assertThat(response.scheduledAt()).isEqualTo(LocalDateTime.of(2026, 5, 10, 10, 0));
  }

  @Test
  void createFromOffer_alreadyExists() {
    when(bookingRepository.findByOfferId(OFFER_ID))
        .thenReturn(Optional.of(buildBooking("awaiting_shop")));

    assertThatThrownBy(() -> bookingService.createFromOffer(OFFER_ID, SLOT_ID, CAR_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Booking already exists for offer");
  }

  @Test
  void createFromOffer_expiredOffer_throwsIllegalState() {
    ShopOfferEntity expiredOffer = buildOffer();
    expiredOffer.setValidUntil(LocalDateTime.now().minusDays(1));
    when(bookingRepository.findByOfferId(OFFER_ID)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(OFFER_ID)).thenReturn(Optional.of(expiredOffer));

    assertThatThrownBy(() -> bookingService.createFromOffer(OFFER_ID, SLOT_ID, CAR_OWNER_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Offer has expired");
  }

  @Test
  void createFromOffer_slotBelongsToDifferentOffer_throwsForbidden() {
    when(bookingRepository.findByOfferId(OFFER_ID)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(OFFER_ID)).thenReturn(Optional.of(buildOffer()));
    when(shopOfferService.getSlotOrThrow(SLOT_ID)).thenReturn(buildSlot(999L));

    assertThatThrownBy(() -> bookingService.createFromOffer(OFFER_ID, SLOT_ID, CAR_OWNER_ID))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Slot does not belong to this offer");
  }

  @Test
  void createFromOffer_wrongUser_throwsForbidden() {
    when(bookingRepository.findByOfferId(OFFER_ID)).thenReturn(Optional.empty());
    when(shopOfferRepository.findById(OFFER_ID)).thenReturn(Optional.of(buildOffer()));
    when(shopOfferService.getSlotOrThrow(SLOT_ID)).thenReturn(buildSlot(OFFER_ID));
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));

    assertThatThrownBy(() -> bookingService.createFromOffer(OFFER_ID, SLOT_ID, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void confirm_success() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("awaiting_shop")));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.confirm(1L, SHOP_OWNER_ID);

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
    RepairShopEntity wrongShop = RepairShopEntity.builder().id(99L).ownerId(SHOP_OWNER_ID).build();
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(wrongShop));

    assertThatThrownBy(() -> bookingService.confirm(1L, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void schedule_success() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    LocalDateTime scheduled = LocalDateTime.of(2026, 4, 12, 10, 0);
    BookingResponse response = bookingService.schedule(1L, scheduled, SHOP_OWNER_ID);

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
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.complete(1L, SHOP_OWNER_ID);

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
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("confirmed")));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.cancel(1L, CAR_OWNER_ID);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void cancel_asShopOwner_success() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(buildBooking("awaiting_shop")));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    BookingResponse response = bookingService.cancel(1L, SHOP_OWNER_ID);

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
    Pageable pageable = PageRequest.of(0, 20);
    when(bookingRepository.findByUserId(CAR_OWNER_ID, pageable))
        .thenReturn(new PageImpl<>(List.of(buildBooking("confirmed"))));

    Page<BookingResponse> result = bookingService.getByUser(CAR_OWNER_ID, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).userId()).isEqualTo(CAR_OWNER_ID);
  }

  @Test
  void getByShop() {
    Pageable pageable = PageRequest.of(0, 20);
    when(repairShopRepository.findById(SHOP_ID)).thenReturn(Optional.of(buildShop()));
    when(bookingRepository.findByShopId(SHOP_ID, pageable))
        .thenReturn(new PageImpl<>(List.of(buildBooking("confirmed"))));

    Page<BookingResponse> result = bookingService.getByShop(SHOP_ID, SHOP_OWNER_ID, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).shopId()).isEqualTo(SHOP_ID);
  }

  @Test
  void getByShop_wrongOwner_throwsForbidden() {
    when(repairShopRepository.findById(SHOP_ID)).thenReturn(Optional.of(buildShop()));

    Pageable pageable = PageRequest.of(0, 20);
    assertThatThrownBy(() -> bookingService.getByShop(SHOP_ID, 999L, pageable))
        .isInstanceOf(ForbiddenException.class);
  }
}
