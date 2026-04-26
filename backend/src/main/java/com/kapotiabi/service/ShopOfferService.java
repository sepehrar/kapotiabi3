package com.kapotiabi.service;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.OfferSlotEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopOfferEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.OfferSlotRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import java.io.IOException;
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

  private final ShopOfferRepository shopOfferRepository;
  private final OfferSlotRepository offerSlotRepository;
  private final JobRequestRepository jobRequestRepository;
  private final RepairShopRepository repairShopRepository;

  @Value("${app.upload-dir}")
  private String uploadDir;

  public List<ShopOfferResponse> getByJobRequest(Long jobRequestId) {
    return shopOfferRepository.findByJobRequestId(jobRequestId).stream()
        .map(
            offer ->
                ShopOfferResponse.from(offer, offerSlotRepository.findByOfferId(offer.getId())))
        .toList();
  }

  public List<ShopOfferResponse> getByShop(Long shopId) {
    return shopOfferRepository.findByShopId(shopId).stream()
        .map(
            offer ->
                ShopOfferResponse.from(offer, offerSlotRepository.findByOfferId(offer.getId())))
        .toList();
  }

  public ShopOfferResponse getById(Long id) {
    ShopOfferEntity offer = findOrThrow(id);
    return ShopOfferResponse.from(offer, offerSlotRepository.findByOfferId(id));
  }

  @Transactional
  public ShopOfferResponse create(Long jobRequestId, ShopOfferRequest req, Long authUserId) {
    JobRequestEntity jobRequest =
        jobRequestRepository
            .findById(jobRequestId)
            .orElseThrow(() -> new NotFoundException("Job request not found: " + jobRequestId));

    RepairShopEntity shop =
        repairShopRepository
            .findByOwnerId(authUserId)
            .orElseThrow(() -> new ForbiddenException("No shop found for this user"));

    ShopOfferEntity offer =
        ShopOfferEntity.builder()
            .jobRequest(jobRequest)
            .shop(shop)
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

    List<OfferSlotEntity> slots = saveSlots(offer.getId(), req.slots());
    return ShopOfferResponse.from(offer, slots);
  }

  @Transactional
  public ShopOfferResponse update(Long id, ShopOfferRequest req, Long authUserId) {
    ShopOfferEntity offer = findOrThrow(id);
    verifyShopOwner(authUserId, offer.getShop().getId());
    offer.setPriceEstimate(req.priceEstimate());
    offer.setBreakdown(req.breakdown());
    offer.setPartsTier(req.partsTier());
    offer.setEstimatedDurationHours(req.estimatedDurationHours());
    offer.setValidUntil(req.validUntil());
    offer = shopOfferRepository.save(offer);

    offerSlotRepository.deleteAll(offerSlotRepository.findByOfferId(id));
    List<OfferSlotEntity> slots = saveSlots(id, req.slots());
    return ShopOfferResponse.from(offer, slots);
  }

  @Transactional
  public ShopOfferResponse reject(Long id, Long authUserId) {
    ShopOfferEntity offer = findOrThrow(id);
    Long jobRequestId = offer.getJobRequest().getId();
    JobRequestEntity jobRequest =
        jobRequestRepository
            .findById(jobRequestId)
            .orElseThrow(() -> new NotFoundException("Job request not found: " + jobRequestId));
    if (!authUserId.equals(jobRequest.getUser().getId())) {
      throw new ForbiddenException("You don't have access to this offer");
    }
    offer.setStatus("rejected");
    offer = shopOfferRepository.save(offer);
    return ShopOfferResponse.from(offer, offerSlotRepository.findByOfferId(id));
  }

  public ShopOfferEntity uploadPdf(Long offerId, MultipartFile file, Long authUserId)
      throws IOException {
    if (!"application/pdf".equals(file.getContentType())) {
      throw new IllegalArgumentException("Only PDF files are allowed");
    }
    ShopOfferEntity offer = findOrThrow(offerId);
    verifyShopOwner(authUserId, offer.getShop().getId());

    Path dir = Paths.get(uploadDir, "offers", offerId.toString());
    Files.createDirectories(dir);

    if (offer.getPdfUrl() != null) {
      Files.deleteIfExists(Paths.get(uploadDir).resolve(offer.getPdfUrl()));
    }

    Path filePath = dir.resolve(offerId + "_offer.pdf");
    file.transferTo(filePath.toFile());
    offer.setPdfUrl("offers/" + offerId + "/" + offerId + "_offer.pdf");
    return shopOfferRepository.save(offer);
  }

  public Resource downloadPdf(Long offerId) throws IOException {
    ShopOfferEntity offer = findOrThrow(offerId);
    if (offer.getPdfUrl() == null) {
      throw new NotFoundException("No PDF available for offer: " + offerId);
    }
    Path path = Paths.get(uploadDir).resolve(offer.getPdfUrl());
    Resource resource = new UrlResource(path.toUri());
    if (!resource.exists()) {
      throw new NotFoundException("PDF file not found for offer: " + offerId);
    }
    return resource;
  }

  public OfferSlotEntity getSlotOrThrow(Long slotId) {
    return offerSlotRepository
        .findById(slotId)
        .orElseThrow(() -> new NotFoundException("Slot not found: " + slotId));
  }

  private List<OfferSlotEntity> saveSlots(Long offerId, List<LocalDateTime> startsAts) {
    if (startsAts == null || startsAts.isEmpty()) {
      return List.of();
    }
    LocalDateTime now = LocalDateTime.now();
    ShopOfferEntity offerRef = shopOfferRepository.getReferenceById(offerId);
    List<OfferSlotEntity> entities =
        startsAts.stream()
            .map(
                startsAt ->
                    OfferSlotEntity.builder()
                        .offer(offerRef)
                        .startsAt(startsAt)
                        .createdAt(now)
                        .build())
            .toList();
    return offerSlotRepository.saveAll(entities);
  }

  private void verifyShopOwner(Long authUserId, Long requiredShopId) {
    RepairShopEntity shop =
        repairShopRepository
            .findByOwnerId(authUserId)
            .orElseThrow(() -> new ForbiddenException("No shop found for this user"));
    if (!shop.getId().equals(requiredShopId)) {
      throw new ForbiddenException("You don't have access to this offer");
    }
  }

  public ShopOfferEntity findOrThrow(Long id) {
    return shopOfferRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Shop offer not found: " + id));
  }
}
