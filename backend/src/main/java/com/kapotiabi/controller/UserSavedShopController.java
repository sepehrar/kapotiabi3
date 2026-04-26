package com.kapotiabi.controller;

import com.kapotiabi.dto.response.PagedResponse;
import com.kapotiabi.dto.response.SavedShopResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.UserSavedShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/users/me/saved-shops")
@RequiredArgsConstructor
@Tag(name = "Saved Shops", description = "User saved shops management")
public class UserSavedShopController {

  private final UserSavedShopService userSavedShopService;

  @GetMapping
  @Operation(summary = "Get saved shops for the authenticated user")
  public ResponseEntity<PagedResponse<SavedShopResponse>> getSaved(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(
        userSavedShopService.getSaved(SecurityUtils.getAuthUserId(), page, size));
  }

  @PostMapping("/{shopId}")
  @Operation(summary = "Save a shop for the authenticated user")
  public ResponseEntity<SavedShopResponse> save(@PathVariable Long shopId) {
    SavedShopResponse saved = userSavedShopService.save(SecurityUtils.getAuthUserId(), shopId);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
    return ResponseEntity.created(location).body(saved);
  }

  @DeleteMapping("/{shopId}")
  @Operation(summary = "Remove a saved shop for the authenticated user")
  public ResponseEntity<Void> unsave(@PathVariable Long shopId) {
    userSavedShopService.unsave(SecurityUtils.getAuthUserId(), shopId);
    return ResponseEntity.noContent().build();
  }
}
