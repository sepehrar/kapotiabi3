package com.kapotiabi.controller;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.service.JobRequestService;
import com.kapotiabi.service.ShopOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-requests")
@RequiredArgsConstructor
@Tag(name = "Job Requests", description = "Car repair job requests submitted by car owners")
public class JobRequestController {

    private final JobRequestService jobRequestService;
    private final ShopOfferService shopOfferService;

    @Operation(summary = "List job requests for a user")
    @GetMapping
    public ResponseEntity<List<JobRequestResponse>> list(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(jobRequestService.getByUser(userId));
    }

    @Operation(summary = "List open job requests visible to shops, filterable by city or county")
    @GetMapping("/available")
    public ResponseEntity<List<JobRequestResponse>> available(@RequestParam(required = false) String city,
                                                               @RequestParam(required = false) String county) {
        return ResponseEntity.ok(jobRequestService.getAvailable(city, county));
    }

    @Operation(summary = "Create a new job request")
    @PostMapping
    public ResponseEntity<JobRequestResponse> create(@Valid @RequestBody JobRequestRequest req) {
        JobRequestResponse created = jobRequestService.create(req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get job request by ID")
    @GetMapping("/{id}")
    public ResponseEntity<JobRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobRequestService.getById(id));
    }

    @Operation(summary = "Update a job request")
    @PutMapping("/{id}")
    public ResponseEntity<JobRequestResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody JobRequestRequest req) {
        return ResponseEntity.ok(jobRequestService.update(id, req));
    }

    @Operation(summary = "Cancel a job request")
    @DeleteMapping("/{id}")
    public ResponseEntity<JobRequestResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(jobRequestService.cancel(id));
    }

    @Operation(summary = "List all offers on a job request")
    @GetMapping("/{id}/offers")
    public ResponseEntity<List<ShopOfferResponse>> getOffers(@PathVariable Long id) {
        return ResponseEntity.ok(shopOfferService.getByJobRequest(id));
    }

    @Operation(summary = "Submit a price offer for a job request (shop owner)")
    @PostMapping("/{id}/offers")
    public ResponseEntity<ShopOfferResponse> submitOffer(@PathVariable Long id,
                                                          @Valid @RequestBody ShopOfferRequest req) {
        ShopOfferResponse created = shopOfferService.create(id, req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/v1/offers/{offerId}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }
}
