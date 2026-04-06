package com.kapotiabi.controller;

import com.kapotiabi.dto.request.GuestEstimateRequest;
import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.service.GuestEstimateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/guest-estimates")
@RequiredArgsConstructor
@Tag(
    name = "Guest Estimates",
    description = "Simplified price estimates for guests without an account")
public class GuestEstimateController {

  private final GuestEstimateService guestEstimateService;

  @Operation(
      summary = "Submit a guest estimate request — returns price ranges from shops in the area")
  @PostMapping
  public ResponseEntity<GuestEstimateResponse> create(
      @Valid @RequestBody GuestEstimateRequest req) {
    GuestEstimateResponse created = guestEstimateService.create(req);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @Operation(summary = "Get a guest estimate and its results by ID")
  @GetMapping("/{id}")
  public ResponseEntity<GuestEstimateResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(guestEstimateService.getById(id));
  }
}
