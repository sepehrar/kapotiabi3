package com.kapotiabi.controller;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.BookingService;
import com.kapotiabi.service.RepairShopService;
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
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Repair Shops", description = "Repair shop management")
public class RepairShopController {

  private final RepairShopService repairShopService;
  private final ShopOfferService shopOfferService;
  private final BookingService bookingService;

  @GetMapping
  @Operation(summary = "Get all shops, optionally filtered by city or county")
  public ResponseEntity<Page<RepairShopResponse>> getAll(
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String county,
      @PageableDefault(size = 20, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(repairShopService.getAll(city, county, pageable));
  }

  @PostMapping
  @Operation(summary = "Create a new repair shop")
  public ResponseEntity<RepairShopResponse> create(@Valid @RequestBody RepairShopRequest req) {
    RepairShopResponse created = repairShopService.create(req, SecurityUtils.getAuthUserId());
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get repair shop by ID")
  public ResponseEntity<RepairShopResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update repair shop")
  public ResponseEntity<RepairShopResponse> update(
      @PathVariable Long id, @Valid @RequestBody RepairShopRequest req) {
    return ResponseEntity.ok(repairShopService.update(id, req, SecurityUtils.getAuthUserId()));
  }

  @GetMapping("/{id}/categories")
  @Operation(summary = "Get service categories for a shop")
  public ResponseEntity<List<ShopServiceCategoryResponse>> getCategories(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getCategories(id));
  }

  @PostMapping("/{id}/categories")
  @Operation(summary = "Add a service category to a shop")
  public ResponseEntity<ShopServiceCategoryResponse> addCategory(
      @PathVariable Long id, @Valid @RequestBody ShopServiceCategoryRequest req) {
    ShopServiceCategoryResponse created = repairShopService.addCategory(id, req);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{catId}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}/categories/{catId}")
  @Operation(summary = "Update a service category")
  public ResponseEntity<ShopServiceCategoryResponse> updateCategory(
      @PathVariable Long id,
      @PathVariable Long catId,
      @Valid @RequestBody ShopServiceCategoryRequest req) {
    return ResponseEntity.ok(repairShopService.updateCategory(id, catId, req));
  }

  @DeleteMapping("/{id}/categories/{catId}")
  @Operation(summary = "Delete a service category")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @PathVariable Long catId) {
    repairShopService.deleteCategory(id, catId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/bookings")
  @Operation(summary = "Get bookings for a shop (shop owner only)")
  public ResponseEntity<Page<BookingResponse>> getBookings(
      @PathVariable Long id, @PageableDefault(size = 20, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(bookingService.getByShop(id, SecurityUtils.getAuthUserId(), pageable));
  }

  @GetMapping("/{id}/offers")
  @Operation(summary = "Get all offers submitted by a shop")
  public ResponseEntity<List<ShopOfferResponse>> getOffers(@PathVariable Long id) {
    return ResponseEntity.ok(shopOfferService.getByShop(id));
  }

  @GetMapping("/{id}/reviews")
  @Operation(summary = "Get reviews for a shop")
  public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getReviews(id));
  }
}
