package com.kapotiabi.service;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.ShopOfferEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ShopOfferService {

  private final ShopOfferRepository shopOfferRepository;
  private final JobRequestRepository jobRequestRepository;
  private final RepairShopRepository repairShopRepository;

  @Value("${app.upload-dir}")
  private String uploadDir;

  public List<ShopOfferResponse> getByJobRequest(Long jobRequestId) {
    return shopOfferRepository.findByJobRequestId(jobRequestId).stream()
        .map(ShopOfferResponse::from)
        .toList();
  }

  public ShopOfferResponse getById(Long id) {
    return ShopOfferResponse.from(findOrThrow(id));
  }

  @Transactional
  public ShopOfferResponse create(Long jobRequestId, ShopOfferRequest req) {
    var jobRequest =
        jobRequestRepository
            .findById(jobRequestId)
            .orElseThrow(() -> new NotFoundException("Job request not found: " + jobRequestId));

    var offer =
        ShopOfferEntity.builder()
            .jobRequest(jobRequest)
            .shop(repairShopRepository.getReferenceById(req.shopId()))
            .priceEstimate(req.priceEstimate())
            .breakdown(req.breakdown())
            .partsTier(req.partsTier())
            .estimatedDurationHours(req.estimatedDurationHours())
            .validUntil(req.validUntil())
            .status("pending")
            .createdAt(LocalDateTime.now())
            .build();
    offer = shopOfferRepository.save(offer);

    if ("pending".equals(jobRequest.getStatus())) {
      jobRequest.setStatus("offers_received");
      jobRequestRepository.save(jobRequest);
    }

    return ShopOfferResponse.from(offer);
  }

  public ShopOfferResponse update(Long id, ShopOfferRequest req) {
    var offer = findOrThrow(id);
    offer.setPriceEstimate(req.priceEstimate());
    offer.setBreakdown(req.breakdown());
    offer.setPartsTier(req.partsTier());
    offer.setEstimatedDurationHours(req.estimatedDurationHours());
    offer.setValidUntil(req.validUntil());
    return ShopOfferResponse.from(shopOfferRepository.save(offer));
  }

  public ShopOfferResponse reject(Long id) {
    var offer = findOrThrow(id);
    offer.setStatus("rejected");
    return ShopOfferResponse.from(shopOfferRepository.save(offer));
  }

  public ShopOfferEntity uploadPdf(Long offerId, MultipartFile file) throws IOException {
    if (!"application/pdf".equals(file.getContentType())) {
      throw new IllegalArgumentException("Only PDF files are allowed");
    }
    var offer = findOrThrow(offerId);
    var dir = Paths.get(uploadDir, "offers", offerId.toString());
    Files.createDirectories(dir);

    if (offer.getPdfUrl() != null) {
      Files.deleteIfExists(Paths.get(uploadDir).resolve(offer.getPdfUrl()));
    }

    var filePath = dir.resolve(offerId + "_offer.pdf");
    file.transferTo(filePath.toFile());
    offer.setPdfUrl("offers/" + offerId + "/" + offerId + "_offer.pdf");
    return shopOfferRepository.save(offer);
  }

  public Resource downloadPdf(Long offerId) throws IOException {
    var offer = findOrThrow(offerId);
    if (offer.getPdfUrl() == null) {
      throw new NotFoundException("No PDF available for offer: " + offerId);
    }
    var path = Paths.get(uploadDir).resolve(offer.getPdfUrl());
    var resource = new UrlResource(path.toUri());
    if (!resource.exists()) {
      throw new NotFoundException("PDF file not found for offer: " + offerId);
    }
    return resource;
  }

  private ShopOfferEntity findOrThrow(Long id) {
    return shopOfferRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Shop offer not found: " + id));
  }
}
