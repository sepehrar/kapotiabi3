package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.CarEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.OfferSlotEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopOfferEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.OfferSlotRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ShopOfferServiceTest {

  @Mock private ShopOfferRepository shopOfferRepository;
  @Mock private OfferSlotRepository offerSlotRepository;
  @Mock private JobRequestRepository jobRequestRepository;
  @Mock private RepairShopRepository repairShopRepository;

  @InjectMocks private ShopOfferService shopOfferService;

  @TempDir Path tempDir;

  private static final Long SHOP_OWNER_ID = 99L;
  private static final Long SHOP_ID = 2L;
  private static final Long CAR_OWNER_ID = 5L;

  @BeforeEach
  void injectUploadDir() {
    ReflectionTestUtils.setField(shopOfferService, "uploadDir", tempDir.toString());
  }

  private ShopOfferEntity buildOffer() {
    return ShopOfferEntity.builder()
        .id(1L)
        .jobRequest(JobRequestEntity.builder().id(10L).build())
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
        .car(CarEntity.builder().id(3L).build())
        .description("Oil change")
        .status("pending")
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

  private OfferSlotEntity buildSlot() {
    return OfferSlotEntity.builder()
        .id(100L)
        .offer(ShopOfferEntity.builder().id(1L).build())
        .startsAt(LocalDateTime.of(2026, 5, 10, 10, 0))
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void getByJobRequest() {
    when(shopOfferRepository.findByJobRequestId(10L)).thenReturn(List.of(buildOffer()));
    when(offerSlotRepository.findByOfferId(1L)).thenReturn(List.of());

    List<ShopOfferResponse> result = shopOfferService.getByJobRequest(10L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().jobRequestId()).isEqualTo(10L);
  }

  @Test
  void getByShop() {
    when(shopOfferRepository.findByShopId(2L)).thenReturn(List.of(buildOffer()));
    when(offerSlotRepository.findByOfferId(1L)).thenReturn(List.of());

    List<ShopOfferResponse> result = shopOfferService.getByShop(2L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().shopId()).isEqualTo(2L);
  }

  @Test
  void getById_includesSlots() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    when(offerSlotRepository.findByOfferId(1L)).thenReturn(List.of(buildSlot()));

    ShopOfferResponse response = shopOfferService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.slots()).hasSize(1);
    assertThat(response.slots().getFirst().startsAt())
        .isEqualTo(LocalDateTime.of(2026, 5, 10, 10, 0));
  }

  @Test
  void create_withSlots_savesSlots() {
    JobRequestEntity jobRequest = buildJobRequest();
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(buildOffer());
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(jobRequest);
    when(offerSlotRepository.saveAll(anyList())).thenReturn(List.of(buildSlot()));

    ShopOfferRequest req =
        new ShopOfferRequest(
            new BigDecimal("100.00"),
            "breakdown",
            "OEM",
            new BigDecimal("2.0"),
            null,
            List.of(LocalDateTime.of(2026, 5, 10, 10, 0)));
    ShopOfferResponse response = shopOfferService.create(10L, req, SHOP_OWNER_ID);

    assertThat(response.slots()).hasSize(1);
    verify(offerSlotRepository).saveAll(anyList());
  }

  @Test
  void create_withoutSlots_returnsEmptySlots() {
    JobRequestEntity jobRequest = buildJobRequest();
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(buildOffer());
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(jobRequest);

    ShopOfferRequest req =
        new ShopOfferRequest(new BigDecimal("100.00"), null, null, null, null, null);
    ShopOfferResponse response = shopOfferService.create(10L, req, SHOP_OWNER_ID);

    assertThat(response.slots()).isEmpty();
  }

  @Test
  void create_noShop_throwsForbidden() {
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.empty());

    ShopOfferRequest req =
        new ShopOfferRequest(new BigDecimal("100.00"), null, null, null, null, null);
    assertThatThrownBy(() -> shopOfferService.create(10L, req, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void update_replacesSlots() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(offerSlotRepository.findByOfferId(1L)).thenReturn(List.of(buildSlot()));
    when(offerSlotRepository.saveAll(anyList())).thenReturn(List.of(buildSlot()));

    ShopOfferRequest req =
        new ShopOfferRequest(
            new BigDecimal("200.00"),
            "new breakdown",
            "OEM",
            null,
            null,
            List.of(LocalDateTime.of(2026, 5, 11, 14, 0)));
    ShopOfferResponse response = shopOfferService.update(1L, req, SHOP_OWNER_ID);

    assertThat(response.priceEstimate()).isEqualByComparingTo(new BigDecimal("200.00"));
    verify(offerSlotRepository).deleteAll(anyList());
    verify(offerSlotRepository).saveAll(anyList());
  }

  @Test
  void update_wrongShop_throwsForbidden() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    RepairShopEntity wrongShop =
        RepairShopEntity.builder()
            .id(99L)
            .owner(UserEntity.builder().id(SHOP_OWNER_ID).build())
            .name("Other Shop")
            .build();
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(wrongShop));

    ShopOfferRequest req =
        new ShopOfferRequest(new BigDecimal("200.00"), null, null, null, null, null);
    assertThatThrownBy(() -> shopOfferService.update(1L, req, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void reject_success() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));
    when(offerSlotRepository.findByOfferId(1L)).thenReturn(List.of());

    ShopOfferResponse response = shopOfferService.reject(1L, CAR_OWNER_ID);

    assertThat(response.status()).isEqualTo("rejected");
  }

  @Test
  void reject_wrongUser_throwsForbidden() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest()));

    assertThatThrownBy(() -> shopOfferService.reject(1L, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void getById_notFound() {
    when(shopOfferRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> shopOfferService.getById(99L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void getSlotOrThrow_notFound() {
    when(offerSlotRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> shopOfferService.getSlotOrThrow(99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }

  // ── PDF upload ────────────────────────────────────────────────────────────

  @Test
  void uploadPdf_nonPdfContentType_throwsIllegalArgument() {
    MockMultipartFile notPdf =
        new MockMultipartFile("file", "doc.txt", "text/plain", "hello".getBytes());

    assertThatThrownBy(() -> shopOfferService.uploadPdf(1L, notPdf, SHOP_OWNER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only PDF files are allowed");
  }

  @Test
  void uploadPdf_validPdf_persistsPdfUrl() throws Exception {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    MockMultipartFile pdf =
        new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-test".getBytes());

    ShopOfferEntity saved = shopOfferService.uploadPdf(1L, pdf, SHOP_OWNER_ID);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    verify(shopOfferRepository).save(any(ShopOfferEntity.class));
  }

  @Test
  void uploadPdf_replacesExistingPdf() throws Exception {
    Path oldDir = Files.createDirectories(tempDir.resolve("offers/1"));
    Path oldFile = oldDir.resolve("old_offer.pdf");
    Files.write(oldFile, "%PDF-old".getBytes());

    ShopOfferEntity offer = buildOffer();
    offer.setPdfUrl("offers/1/old_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    MockMultipartFile pdf =
        new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-new".getBytes());

    ShopOfferEntity saved = shopOfferService.uploadPdf(1L, pdf, SHOP_OWNER_ID);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    assertThat(oldFile).doesNotExist();
  }

  // ── PDF download ──────────────────────────────────────────────────────────

  @Test
  void downloadPdf_noPdfUrl_throwsNotFound() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));

    assertThatThrownBy(() -> shopOfferService.downloadPdf(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No PDF available for offer");
  }

  @Test
  void downloadPdf_fileNotOnDisk_throwsNotFound() {
    ShopOfferEntity offer = buildOffer();
    offer.setPdfUrl("offers/1/1_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    assertThatThrownBy(() -> shopOfferService.downloadPdf(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("PDF file not found for offer");
  }

  @Test
  void downloadPdf_fileExists_returnsResource() throws Exception {
    Path pdfDir = Files.createDirectories(tempDir.resolve("offers/1"));
    Files.write(pdfDir.resolve("1_offer.pdf"), "%PDF-data".getBytes());

    ShopOfferEntity offer = buildOffer();
    offer.setPdfUrl("offers/1/1_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    Resource resource = shopOfferService.downloadPdf(1L);

    assertThat(resource.exists()).isTrue();
  }
}
