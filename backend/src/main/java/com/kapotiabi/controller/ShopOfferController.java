package com.kapotiabi.controller;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.ShopOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(name = "Shop Offers", description = "Price offers submitted by repair shops")
public class ShopOfferController {

  private final ShopOfferService shopOfferService;
  private final BookingService bookingService;

  @Operation(summary = "Get offer by ID")
  @GetMapping("/{id}")
  public ResponseEntity<ShopOfferResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.getById(id));
  }

  @Operation(summary = "Update an offer")
  @PutMapping("/{id}")
  public ResponseEntity<ShopOfferResponse> update(
      @PathVariable Long id, @Valid @RequestBody ShopOfferRequest req) {
    return ResponseEntity.ok(shopOfferService.update(id, req));
  }

  @Operation(summary = "Accept an offer — creates a booking")
  @PostMapping("/{id}/accept")
  public ResponseEntity<BookingResponse> accept(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.createFromOffer(id));
  }

  @Operation(summary = "Reject an offer")
  @PostMapping("/{id}/reject")
  public ResponseEntity<ShopOfferResponse> reject(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.reject(id));
  }

  @Operation(summary = "Upload a PDF price offer (multipart/form-data, field: file)")
  @PostMapping(value = "/{id}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadPdf(
      @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
    shopOfferService.uploadPdf(id, file);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Download the PDF price offer")
  @GetMapping("/{id}/pdf")
  public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) throws IOException {
    Resource resource = shopOfferService.downloadPdf(id);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"offer_" + id + ".pdf\"")
        .body(resource);
  }
}
