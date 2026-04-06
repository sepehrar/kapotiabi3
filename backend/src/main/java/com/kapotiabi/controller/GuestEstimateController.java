package com.kapotiabi.controller;

import com.kapotiabi.dto.request.GuestEstimateRequest;
import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.entity.GuestEstimate;
import com.kapotiabi.entity.GuestEstimateResult;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.GuestEstimateRepository;
import com.kapotiabi.repository.GuestEstimateResultRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import com.kapotiabi.repository.RepairShopRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/guest-estimates")
@RequiredArgsConstructor
@Tag(name = "Guest Estimates", description = "Simplified price estimates for guests without an account")
public class GuestEstimateController {

    private final GuestEstimateRepository estimateRepository;
    private final GuestEstimateResultRepository resultRepository;
    private final ShopServiceCategoryRepository categoryRepository;
    private final RepairShopRepository shopRepository;

    @Operation(summary = "Submit a guest estimate request — returns price ranges from shops in the area")
    @PostMapping
    public ResponseEntity<GuestEstimateResponse> create(@Valid @RequestBody GuestEstimateRequest req) {
        GuestEstimate estimate = new GuestEstimate();
        estimate.setPlateNumber(req.plateNumber());
        estimate.setDescription(req.description());
        estimate.setCity(req.city());
        estimate.setCounty(req.county());
        estimate.setCreatedAt(LocalDateTime.now());
        estimate = estimateRepository.save(estimate);

        List<com.kapotiabi.entity.RepairShop> shops = req.city() != null
                ? shopRepository.findByCity(req.city())
                : req.county() != null ? shopRepository.findByCounty(req.county())
                : shopRepository.findAll();

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        for (var shop : shops) {
            var categories = categoryRepository.findByShopId(shop.getId());
            if (categories.isEmpty()) continue;

            BigDecimal shopMin = categories.stream()
                    .filter(c -> c.getPriceFrom() != null)
                    .map(c -> c.getPriceFrom())
                    .min(BigDecimal::compareTo).orElse(null);
            BigDecimal shopMax = categories.stream()
                    .filter(c -> c.getPriceTo() != null)
                    .map(c -> c.getPriceTo())
                    .max(BigDecimal::compareTo).orElse(null);

            GuestEstimateResult result = new GuestEstimateResult();
            result.setGuestEstimateId(estimate.getId());
            result.setShopId(shop.getId());
            result.setPriceFrom(shopMin);
            result.setPriceTo(shopMax);
            resultRepository.save(result);

            if (shopMin != null && (minPrice == null || shopMin.compareTo(minPrice) < 0)) minPrice = shopMin;
            if (shopMax != null && (maxPrice == null || shopMax.compareTo(maxPrice) > 0)) maxPrice = shopMax;
        }

        estimate.setPriceFrom(minPrice);
        estimate.setPriceTo(maxPrice);
        estimate = estimateRepository.save(estimate);

        List<GuestEstimateResult> results = resultRepository.findByGuestEstimateId(estimate.getId());
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(estimate.getId()).toUri();
        return ResponseEntity.created(location).body(GuestEstimateResponse.from(estimate, results));
    }

    @Operation(summary = "Get a guest estimate and its results by ID")
    @GetMapping("/{id}")
    public ResponseEntity<GuestEstimateResponse> getById(@PathVariable Long id) {
        GuestEstimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estimate not found: " + id));
        List<GuestEstimateResult> results = resultRepository.findByGuestEstimateId(id);
        return ResponseEntity.ok(GuestEstimateResponse.from(estimate, results));
    }
}
