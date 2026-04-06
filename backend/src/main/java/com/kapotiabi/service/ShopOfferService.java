package com.kapotiabi.service;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.ShopOfferEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private final ShopOfferRepository offerRepository;
  private final JobRequestRepository jobRequestRepository;

  @Value("${app.upload-dir}")
  private String uploadDir;

  public List<ShopOfferResponse> getByJobRequest(Long jobRequestId) {
    return offerRepository.findByJobRequestId(jobRequestId).stream()
        .map(ShopOfferResponse::from)
        .toList();
  }

  public ShopOfferResponse getById(Long id) {
    return ShopOfferResponse.from(findOrThrow(id));
  }

  @Transactional
  public ShopOfferResponse create(Long jobRequestId, ShopOfferRequest req) {
    JobRequestEntity jr =
        jobRequestRepository
            .findById(jobRequestId)
            .orElseThrow(() -> new NotFoundException("Job request not found: " + jobRequestId));

    ShopOfferEntity offer = new ShopOfferEntity();
    offer.setJobRequestId(jobRequestId);
    offer.setShopId(req.shopId());
    offer.setPriceEstimate(req.priceEstimate());
    offer.setBreakdown(req.breakdown());
    offer.setPartsTier(req.partsTier());
    offer.setEstimatedDurationHours(req.estimatedDurationHours());
    offer.setValidUntil(req.validUntil());
    offer.setStatus("pending");
    offer.setCreatedAt(LocalDateTime.now());

    if ("pending".equals(jr.getStatus())) {
      jr.setStatus("offers_received");
      jobRequestRepository.save(jr);
    }

    return ShopOfferResponse.from(offerRepository.save(offer));
  }

  public ShopOfferResponse update(Long id, ShopOfferRequest req) {
    ShopOfferEntity offer = findOrThrow(id);
    offer.setPriceEstimate(req.priceEstimate());
    offer.setBreakdown(req.breakdown());
    offer.setPartsTier(req.partsTier());
    offer.setEstimatedDurationHours(req.estimatedDurationHours());
    offer.setValidUntil(req.validUntil());
    return ShopOfferResponse.from(offerRepository.save(offer));
  }

  public ShopOfferResponse reject(Long id) {
    ShopOfferEntity offer = findOrThrow(id);
    offer.setStatus("rejected");
    return ShopOfferResponse.from(offerRepository.save(offer));
  }

  public ShopOfferEntity uploadPdf(Long offerId, MultipartFile file) throws IOException {
    if (!"application/pdf".equals(file.getContentType())) {
      throw new IllegalArgumentException("Only PDF files are allowed");
    }
    ShopOfferEntity offer = findOrThrow(offerId);

    Path dir = Paths.get(uploadDir, "offers", offerId.toString());
    Files.createDirectories(dir);

    if (offer.getPdfUrl() != null) {
      Files.deleteIfExists(Paths.get(uploadDir).resolve(offer.getPdfUrl()));
    }

    Path dest = dir.resolve(offerId + "_offer.pdf");
    file.transferTo(dest);
    offer.setPdfUrl("offers/" + offerId + "/" + offerId + "_offer.pdf");
    return offerRepository.save(offer);
  }

  public Resource downloadPdf(Long offerId) throws MalformedURLException {
    ShopOfferEntity offer = findOrThrow(offerId);
    if (offer.getPdfUrl() == null) {
      throw new IllegalArgumentException("No PDF uploaded for offer: " + offerId);
    }
    Path path = Paths.get(uploadDir).resolve(offer.getPdfUrl());
    Resource resource = new UrlResource(path.toUri());
    if (!resource.exists()) {
      throw new IllegalArgumentException("PDF file missing on disk for offer: " + offerId);
    }
    return resource;
  }

  private ShopOfferEntity findOrThrow(Long id) {
    return offerRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Offer not found: " + id));
  }
}
