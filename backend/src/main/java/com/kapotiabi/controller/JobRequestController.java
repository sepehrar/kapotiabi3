package com.kapotiabi.controller;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.JobRequestService;
import com.kapotiabi.service.ShopOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/job-requests")
@RequiredArgsConstructor
@Tag(name = "Job Requests", description = "Job request management")
public class JobRequestController {

  private final JobRequestService jobRequestService;
  private final ShopOfferService shopOfferService;

  @GetMapping
  @Operation(summary = "Get job requests for the authenticated user")
  public ResponseEntity<List<JobRequestResponse>> getByUser() {
    return ResponseEntity.ok(jobRequestService.getByUser(SecurityUtils.getAuthUserId()));
  }

  @GetMapping("/available")
  @Operation(
      summary = "Get available (pending) job requests, optionally filtered by city or county")
  public ResponseEntity<Page<JobRequestResponse>> getAvailable(
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String county,
      @PageableDefault(size = 20, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(jobRequestService.getAvailable(city, county, pageable));
  }

  @PostMapping
  @Operation(summary = "Create a new job request")
  public ResponseEntity<JobRequestResponse> create(@Valid @RequestBody JobRequestRequest req) {
    JobRequestResponse created = jobRequestService.create(req);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get job request by ID")
  public ResponseEntity<JobRequestResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(jobRequestService.getById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update job request")
  public ResponseEntity<JobRequestResponse> update(
      @PathVariable Long id, @Valid @RequestBody JobRequestRequest req) {
    return ResponseEntity.ok(jobRequestService.update(id, req));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Cancel job request (soft delete)")
  public ResponseEntity<JobRequestResponse> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(jobRequestService.cancel(id));
  }

  @GetMapping("/{id}/offers")
  @Operation(summary = "Get all offers for a job request")
  public ResponseEntity<List<ShopOfferResponse>> getOffers(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.getByJobRequest(id));
  }

  @PostMapping("/{id}/offers")
  @Operation(summary = "Submit an offer for a job request (shop owner only)")
  public ResponseEntity<ShopOfferResponse> createOffer(
      @PathVariable Long id, @Valid @RequestBody ShopOfferRequest req) {
    ShopOfferResponse created = shopOfferService.create(id, req, SecurityUtils.getAuthUserId());
    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/offers/{offerId}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }
}
