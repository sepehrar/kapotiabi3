package com.kapotiabi.controller;

import com.kapotiabi.dto.request.CarRequest;
import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.security.SecurityUtils;
import com.kapotiabi.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "Car management")
public class CarController {

  private final CarService carService;

  @GetMapping
  @Operation(summary = "Get cars by owner ID")
  public ResponseEntity<List<CarResponse>> getByOwner() {
    return ResponseEntity.ok(carService.getByOwner(SecurityUtils.getAuthUserId()));
  }

  @PostMapping
  @Operation(summary = "Create a new car")
  public ResponseEntity<CarResponse> create(@Valid @RequestBody CarRequest req) {
    CarResponse created = carService.create(req, SecurityUtils.getAuthUserId());
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get car by ID")
  public ResponseEntity<CarResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(carService.getById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update car")
  public ResponseEntity<CarResponse> update(
      @PathVariable Long id, @Valid @RequestBody CarRequest req) {
    return ResponseEntity.ok(carService.update(id, req, SecurityUtils.getAuthUserId()));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete car")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    carService.delete(id, SecurityUtils.getAuthUserId());
    return ResponseEntity.noContent().build();
  }
}
