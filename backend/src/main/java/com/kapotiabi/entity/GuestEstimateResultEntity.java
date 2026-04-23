package com.kapotiabi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guest_estimate_results")
public class GuestEstimateResultEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "guest_estimate_id", nullable = false)
  private GuestEstimateEntity guestEstimate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "shop_id", nullable = false)
  private RepairShopEntity shop;

  @Column(name = "price_from")
  private BigDecimal priceFrom;

  @Column(name = "price_to")
  private BigDecimal priceTo;
}
