package com.kapotiabi.service;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.JobRequest;
import com.kapotiabi.entity.ShopOffer;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopOfferService {

    private final ShopOfferRepository offerRepository;
    private final JobRequestRepository jobRequestRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public List<ShopOfferResponse> getByJobRequest(Long jobRequestId) {
        return offerRepository.findByJobRequestId(jobRequestId).stream().map(ShopOfferResponse::from).toList();
    }

    public ShopOfferResponse getById(Long id) {
        return ShopOfferResponse.from(findOrThrow(id));
    }

    public ShopOfferResponse create(Long jobRequestId, ShopOfferRequest req) {
        JobRequest jr = jobRequestRepository.findById(jobRequestId)
                .orElseThrow(() -> new NotFoundException("Job request not found: " + jobRequestId));

        ShopOffer offer = new ShopOffer();
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
        ShopOffer offer = findOrThrow(id);
        offer.setPriceEstimate(req.priceEstimate());
        offer.setBreakdown(req.breakdown());
        offer.setPartsTier(req.partsTier());
        offer.setEstimatedDurationHours(req.estimatedDurationHours());
        offer.setValidUntil(req.validUntil());
        return ShopOfferResponse.from(offerRepository.save(offer));
    }

    public ShopOfferResponse reject(Long id) {
        ShopOffer offer = findOrThrow(id);
        offer.setStatus("rejected");
        return ShopOfferResponse.from(offerRepository.save(offer));
    }

    public ShopOffer uploadPdf(Long offerId, MultipartFile file) throws IOException {
        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        ShopOffer offer = findOrThrowEntity(offerId);

        Path dir = Paths.get(uploadDir, "offers", offerId.toString());
        Files.createDirectories(dir);

        if (offer.getPdfUrl() != null) {
            Files.deleteIfExists(Paths.get(offer.getPdfUrl()));
        }

        Path dest = dir.resolve(offerId + "_offer.pdf");
        file.transferTo(dest);
        offer.setPdfUrl(dest.toString());
        return offerRepository.save(offer);
    }

    public Resource downloadPdf(Long offerId) throws MalformedURLException {
        ShopOffer offer = findOrThrowEntity(offerId);
        if (offer.getPdfUrl() == null) {
            throw new IllegalArgumentException("No PDF uploaded for offer: " + offerId);
        }
        Path path = Paths.get(offer.getPdfUrl());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new IllegalArgumentException("PDF file missing on disk for offer: " + offerId);
        }
        return resource;
    }

    private ShopOffer findOrThrowEntity(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offer not found: " + id));
    }

    private ShopOffer findOrThrow(Long id) {
        return findOrThrowEntity(id);
    }
}
