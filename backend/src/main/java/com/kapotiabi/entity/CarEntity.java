package com.kapotiabi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class CarEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "owner_id", nullable = false)
  private Long ownerId;

  @Column(name = "plate_number", nullable = false)
  private String plateNumber;

  @Column(name = "make")
  private String make;

  @Column(name = "model")
  private String model;

  @Column(name = "year")
  private Integer year;

  @Column(name = "engine")
  private String engine;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
