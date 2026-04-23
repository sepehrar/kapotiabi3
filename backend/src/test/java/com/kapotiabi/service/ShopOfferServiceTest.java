package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.entity.*;
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

  @BeforeEach
  void injectUploadDir() {
    ReflectionTestUtils.setField(shopOfferService, "uploadDir", tempDir.toString());
  }

  private ShopOfferEntity buildOffer() {
    return ShopOfferEntity.builder()
        .id(1L)
        .jobRequest(JobRequestEntity.builder().id(10L).build())
        .shop(RepairShopEntity.builder().id(2L).build())
        .priceEstimate(new BigDecimal("100.00"))
        .status("pending")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private JobRequestEntity buildJobRequest(String status) {
    return JobRequestEntity.builder()
        .id(10L)
        .user(UserEntity.builder().id(5L).build())
        .car(CarEntity.builder().id(3L).build())
        .description("Oil change")
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  void getByJobRequest() {
    when(shopOfferRepository.findByJobRequestId(10L)).thenReturn(List.of(buildOffer()));

    var result = shopOfferService.getByJobRequest(10L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().jobRequestId()).isEqualTo(10L);
  }

  @Test
  void getById() {
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(buildOffer()));

    var response = shopOfferService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
  }

  @Test
  void create_updatesPending() {
    var jobRequest = buildJobRequest("pending");
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.getReferenceById(2L))
        .thenReturn(RepairShopEntity.builder().id(2L).build());
    var savedOffer = buildOffer();
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(savedOffer);
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(jobRequest);

    var req =
        new ShopOfferRequest(
            2L, new BigDecimal("100.00"), "breakdown", "OEM", new BigDecimal("2.0"), null);
    var response = shopOfferService.create(10L, req);

    assertThat(response.shopId()).isEqualTo(2L);
    verify(jobRequestRepository).save(any(JobRequestEntity.class));
  }

  @Test
  void create_alreadyOffers() {
    var jobRequest = buildJobRequest("offers_received");
    when(jobRequestRepository.findById(10L)).thenReturn(Optional.of(jobRequest));
    when(repairShopRepository.getReferenceById(2L))
        .thenReturn(RepairShopEntity.builder().id(2L).build());
    var savedOffer = buildOffer();
    when(shopOfferRepository.save(any(ShopOfferEntity.class))).thenReturn(savedOffer);

    var req = new ShopOfferRequest(2L, new BigDecimal("100.00"), null, null, null, null);
    var response = shopOfferService.create(10L, req);

    // Status is already offers_received, no update needed
    assertThat(response).isNotNull();
  }

  @Test
  void reject() {
    var offer = buildOffer();
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var response = shopOfferService.reject(1L);

    assertThat(response.status()).isEqualTo("rejected");
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

    assertThatThrownBy(() -> shopOfferService.uploadPdf(1L, notPdf))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only PDF files are allowed");
  }

  @Test
  void uploadPdf_validPdf_persistsPdfUrl() throws Exception {
    var offer = buildOffer();
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var pdf = new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-test".getBytes());

    var saved = shopOfferService.uploadPdf(1L, pdf);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    verify(shopOfferRepository).save(any(ShopOfferEntity.class));
  }

  @Test
  void uploadPdf_replacesExistingPdf() throws Exception {
    // Create the "old" file on disk so deleteIfExists has a real file
    var oldDir = Files.createDirectories(tempDir.resolve("offers/1"));
    var oldFile = oldDir.resolve("old_offer.pdf");
    Files.write(oldFile, "%PDF-old".getBytes());

    var offer = buildOffer();
    offer.setPdfUrl("offers/1/old_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
    when(shopOfferRepository.save(any(ShopOfferEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var pdf = new MockMultipartFile("file", "offer.pdf", "application/pdf", "%PDF-new".getBytes());

    var saved = shopOfferService.uploadPdf(1L, pdf);

    assertThat(saved.getPdfUrl()).isEqualTo("offers/1/1_offer.pdf");
    assertThat(oldFile).doesNotExist();
  }

  // ── PDF download ──────────────────────────────────────────────────────────

  @Test
  void downloadPdf_noPdfUrl_throwsNotFound() {
    var offer = buildOffer();
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    assertThatThrownBy(() -> shopOfferService.downloadPdf(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("No PDF available for offer");
  }

  @Test
  void downloadPdf_fileNotOnDisk_throwsNotFound() {
    var offer = buildOffer();
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

    var offer = buildOffer();
    offer.setPdfUrl("offers/1/1_offer.pdf");
    when(shopOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

    var resource = shopOfferService.downloadPdf(1L);

    assertThat(resource.exists()).isTrue();
  }
}
