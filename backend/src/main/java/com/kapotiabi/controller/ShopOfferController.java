package com.kapotiabi.controller;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.security.SecurityUtils;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(name = "Shop Offers", description = "Shop offer management")
public class ShopOfferController {

  private final ShopOfferService shopOfferService;
  private final BookingService bookingService;

  @GetMapping("/{id}")
  @Operation(summary = "Get offer by ID")
  public ResponseEntity<ShopOfferResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.getById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update offer (shop owner only)")
  public ResponseEntity<ShopOfferResponse> update(
      @PathVariable Long id, @Valid @RequestBody ShopOfferRequest req) {
    return ResponseEntity.ok(shopOfferService.update(id, req, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/accept")
  @Operation(summary = "Accept an offer and create a booking (car owner only)")
  public ResponseEntity<BookingResponse> accept(@PathVariable Long id) {
    return ResponseEntity.ok(bookingService.createFromOffer(id, SecurityUtils.getAuthUserId()));
  }

  @PostMapping("/{id}/reject")
  @Operation(summary = "Reject an offer (car owner only)")
  public ResponseEntity<ShopOfferResponse> reject(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.reject(id, SecurityUtils.getAuthUserId()));
  }

  @PostMapping(value = "/{id}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a PDF for an offer (shop owner only)")
  public ResponseEntity<Void> uploadPdf(
      @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
    shopOfferService.uploadPdf(id, file, SecurityUtils.getAuthUserId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/pdf")
  @Operation(summary = "Download the PDF for an offer")
  public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) throws IOException {
    Resource resource = shopOfferService.downloadPdf(id);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"offer_" + id + ".pdf\"")
        .body(resource);
  }
}
