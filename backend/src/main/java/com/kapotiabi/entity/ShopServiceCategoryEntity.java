package com.kapotiabi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
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
@Table(name = "shop_service_categories")
public class ShopServiceCategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "category", nullable = false)
  private String category;

  @Column(name = "price_from")
  private BigDecimal priceFrom;

  @Column(name = "price_to")
  private BigDecimal priceTo;

  @Column(name = "parts_tier")
  private String partsTier;

  @Column(name = "last_updated")
  private LocalDateTime lastUpdated;
}
