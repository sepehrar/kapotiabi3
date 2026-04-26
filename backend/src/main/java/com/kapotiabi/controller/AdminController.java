package com.kapotiabi.controller;

import com.kapotiabi.dto.request.AdminShopStatusRequest;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.service.RepairShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin-only operations")
public class AdminController {

  private final RepairShopService repairShopService;

  @GetMapping("/shops")
  @Operation(summary = "List all shops (optionally filtered by status: draft, active, rejected)")
  public ResponseEntity<List<RepairShopResponse>> listShops(
      @RequestParam(required = false) String status) {
    return ResponseEntity.ok(repairShopService.getAllForAdmin(status));
  }

  @PatchMapping("/shops/{id}/status")
  @Operation(summary = "Approve or reject a shop (action: approve | reject)")
  public ResponseEntity<RepairShopResponse> updateShopStatus(
      @PathVariable Long id, @Valid @RequestBody AdminShopStatusRequest req) {
    return ResponseEntity.ok(repairShopService.updateShopStatus(id, req.action()));
  }
}
