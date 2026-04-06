package com.kapotiabi.controller;

import com.kapotiabi.dto.request.CarRequest;
import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "Car management for car owners")
public class CarController {

    private final CarService carService;

    @Operation(summary = "List cars by owner")
    @GetMapping
    public ResponseEntity<List<CarResponse>> list(@RequestParam Long ownerId) {
        return ResponseEntity.ok(carService.getByOwner(ownerId));
    }

    @Operation(summary = "Add a new car")
    @PostMapping
    public ResponseEntity<CarResponse> create(@Valid @RequestBody CarRequest req) {
        CarResponse created = carService.create(req);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get car by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @Operation(summary = "Update car details")
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody CarRequest req) {
        return ResponseEntity.ok(carService.update(id, req));
    }

    @Operation(summary = "Delete a car")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
