package com.kapotiabi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guest_estimates")
public class GuestEstimateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "plate_number")
  private String plateNumber;

  private String description;
  private String city;
  private String county;

  @Column(name = "price_from")
  private BigDecimal priceFrom;

  @Column(name = "price_to")
  private BigDecimal priceTo;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
