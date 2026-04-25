package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.entity.*;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ShopOfferServiceTest {

  @Mock private ShopOfferRepository shopOfferRepository;
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

  private ShopOfferEntity buildOffer(String status) {
    return ShopOfferEntity.builder()
        .id(1L)
        .jobRequest(buildJobRequest("offers_received"))
        .shop(RepairShopEntity.builder().id(SHOP_ID).build())
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest(String status) {
    return JobRequestEntity.builder()
        .id(10L)
        .user(UserEntity.builder().id(CAR_OWNER_ID).build())
        .car(CarEntity.builder().id(3L).build())
        .description("Oil change")
        .status(status)
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
  void getByJobRequest() {
    when(shopOfferRepository.findByJobRequestId(10L)).thenReturn(List.of(buildOffer("pending")));

    var result = shopOfferService.getByJobRequest(10L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().jobRequestId()).isEqualTo(10L);
  }

  @Test
  void getByShop() {
    when(shopOfferRepository.findByShopId(SHOP_ID)).thenReturn(List.of(buildOffer("pending")));

    var result = shopOfferService.getByShop(SHOP_ID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().shopId()).isEqualTo(SHOP_ID);
  }

  @Test
  void getById() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer("pending")));

    var response = shopOfferService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
  }

  @Test
  void create_updatesPending() {
    var jobRequest = buildJobRequest("pending");
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    var savedOffer = buildOffer("pending");
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(savedOffer);
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(jobRequest);

    var req =
        new ShopOfferRequest(
            SHOP_ID, new BigDecimal("100.00"), "breakdown", "OEM", new BigDecimal("2.0"), null);
    var response = shopOfferService.create(10L, req, SHOP_OWNER_ID);

    assertThat(response.shopId()).isEqualTo(SHOP_ID);
    verify(jobRequestRepository).save(any(JobRequestEntity.class));
  }

  @Test
  void create_alreadyOffers() {
    var jobRequest = buildJobRequest("offers_received");
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    var savedOffer = buildOffer("pending");
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(savedOffer);

    var req = new ShopOfferRequest(SHOP_ID, new BigDecimal("100.00"), null, null, null, null);
    var response = shopOfferService.create(10L, req, SHOP_OWNER_ID);

    assertThat(response).isNotNull();
  }

  @Test
  void create_noShop_throwsForbidden() {
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(buildJobRequest("pending")));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.empty());

    var req = new ShopOfferRequest(SHOP_ID, new BigDecimal("100.00"), null, null, null, null);
    assertThatThrownBy(() -> shopOfferService.create(10L, req, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void update_success() {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var req =
        new ShopOfferRequest(SHOP_ID, new BigDecimal("200.00"), "new breakdown", "OEM", null, null);
    var response = shopOfferService.update(1L, req, SHOP_OWNER_ID);

    assertThat(response.priceEstimate()).isEqualByComparingTo(new BigDecimal("200.00"));
  }

  @Test
  void update_wrongShop_throwsForbidden() {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    RepairShopEntity wrongShop =
        RepairShopEntity.builder()
            .id(99L)
            .owner(UserEntity.builder().id(SHOP_OWNER_ID).build())
            .name("Other Shop")
            .build();
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(wrongShop));

    var req = new ShopOfferRequest(SHOP_ID, new BigDecimal("200.00"), null, null, null, null);
    assertThatThrownBy(() -> shopOfferService.update(1L, req, SHOP_OWNER_ID))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void reject_success() {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var response = shopOfferService.reject(1L, CAR_OWNER_ID);

    assertThat(response.status()).isEqualTo("rejected");
  }

  @Test
  void reject_wrongUser_throwsForbidden() {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    assertThatThrownBy(() -> shopOfferService.reject(1L, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void getById_notFound() {
    when(shopOfferRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> shopOfferService.getById(99L)).isInstanceOf(NotFoundException.class);
  }

  // ── PDF upload ────────────────────────────────────────────────────────────

  @Test
  void uploadPdf_nonPdfContentType_throwsIllegalArgument() {
    var notPdf = new MockMultipartFile("file", "doc.txt", "text/plain", "hello".getBytes());

    assertThatThrownBy(() -> shopOfferService.uploadPdf(1L, notPdf, SHOP_OWNER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only PDF files are allowed");
  }

  @Test
  void uploadPdf_validPdf_persistsPdfUrl() throws Exception {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var pdf = new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-test".getBytes());

    var saved = shopOfferService.uploadPdf(1L, pdf, SHOP_OWNER_ID);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    verify(shopOfferRepository).save(any(ShopOfferEntity.class));
  }

  @Test
  void uploadPdf_replacesExistingPdf() throws Exception {
    // Create the "old" file on disk so deleteIfExists has a real file
    var oldDir = Files.createDirectories(tempDir.resolve("offers/1"));
    var oldFile = oldDir.resolve("old_offer.pdf");
    Files.write(oldFile, "%PDF-old".getBytes());

    var offer = buildOffer("pending");
    offer.setPdfUrl("offers/1/old_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(repairShopRepository.findByOwnerId(SHOP_OWNER_ID)).thenReturn(Optional.of(buildShop()));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var pdf = new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-new".getBytes());

    var saved = shopOfferService.uploadPdf(1L, pdf, SHOP_OWNER_ID);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    assertThat(oldFile).doesNotExist();
  }

  // ── PDF download ──────────────────────────────────────────────────────────

  @Test
  void downloadPdf_noPdfUrl_throwsNotFound() {
    var offer = buildOffer("pending");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    assertThatThrownBy(() -> shopOfferService.downloadPdf(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No PDF available for offer");
  }

  @Test
  void downloadPdf_fileNotOnDisk_throwsNotFound() {
    var offer = buildOffer("pending");
    offer.setPdfUrl("offers/1/1_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    assertThatThrownBy(() -> shopOfferService.downloadPdf(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("PDF file not found for offer");
  }

  @Test
  void downloadPdf_fileExists_returnsResource() throws Exception {
    var pdfDir = Files.createDirectories(tempDir.resolve("offers/1"));
    Files.write(pdfDir.resolve("1_offer.pdf"), "%PDF-data".getBytes());

    var offer = buildOffer("pending");
    offer.setPdfUrl("offers/1/1_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    var resource = shopOfferService.downloadPdf(1L);

    assertThat(resource.exists()).isTrue();
  }
}
