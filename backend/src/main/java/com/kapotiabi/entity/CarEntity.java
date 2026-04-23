package com.kapotiabi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class CarEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private UserEntity owner;

  @Column(name = "plate_number", nullable = false)
  private String plateNumber;

  private String make;
  private String model;
  private Integer year;
  private String engine;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
