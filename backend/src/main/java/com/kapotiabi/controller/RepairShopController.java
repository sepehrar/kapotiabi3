package com.kapotiabi.controller;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.service.RepairShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Repair Shops", description = "Shop profiles and service categories")
public class RepairShopController {

  private final RepairShopService repairShopService;

  @Operation(summary = "List all shops, optionally filtered by city or county")
  @GetMapping
  public ResponseEntity<List<RepairShopResponse>> list(
      @RequestParam(required = false) String city, @RequestParam(required = false) String county) {
    return ResponseEntity.ok(repairShopService.getAll(city, county));
  }

  @Operation(summary = "Create a new repair shop")
  @PostMapping
  public ResponseEntity<RepairShopResponse> create(@Valid @RequestBody RepairShopRequest req) {
    RepairShopResponse created = repairShopService.create(req);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @Operation(summary = "Get shop by ID")
  @GetMapping("/{id}")
  public ResponseEntity<RepairShopResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getById(id));
  }

  @Operation(summary = "Update shop details")
  @PutMapping("/{id}")
  public ResponseEntity<RepairShopResponse> update(
      @PathVariable Long id, @Valid @RequestBody RepairShopRequest req) {
    return ResponseEntity.ok(repairShopService.update(id, req));
  }

  @Operation(summary = "List service categories for a shop")
  @GetMapping("/{id}/categories")
  public ResponseEntity<List<ShopServiceCategoryResponse>> getCategories(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getCategories(id));
  }

  @Operation(summary = "Add a service category to a shop")
  @PostMapping("/{id}/categories")
  public ResponseEntity<ShopServiceCategoryResponse> addCategory(
      @PathVariable Long id, @Valid @RequestBody ShopServiceCategoryRequest req) {
    ShopServiceCategoryResponse created = repairShopService.addCategory(id, req);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{catId}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @Operation(summary = "Update a service category")
  @PutMapping("/{id}/categories/{catId}")
  public ResponseEntity<ShopServiceCategoryResponse> updateCategory(
      @PathVariable Long id,
      @PathVariable Long catId,
      @Valid @RequestBody ShopServiceCategoryRequest req) {
    return ResponseEntity.ok(repairShopService.updateCategory(id, catId, req));
  }

  @Operation(summary = "Remove a service category")
  @DeleteMapping("/{id}/categories/{catId}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @PathVariable Long catId) {
    repairShopService.deleteCategory(id, catId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get all reviews for a shop")
  @GetMapping("/{id}/reviews")
  public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long id) {
    return ResponseEntity.ok(repairShopService.getReviews(id));
  }
}
