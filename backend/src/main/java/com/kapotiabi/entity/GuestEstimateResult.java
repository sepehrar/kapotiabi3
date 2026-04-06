package com.kapotiabi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "guest_estimate_results")
public class GuestEstimateResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_estimate_id", nullable = false)
    private Long guestEstimateId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "price_from")
    private BigDecimal priceFrom;

    @Column(name = "price_to")
    private BigDecimal priceTo;
}
