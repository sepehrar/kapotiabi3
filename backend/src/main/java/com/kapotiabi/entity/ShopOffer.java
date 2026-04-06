package com.kapotiabi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shop_offers")
public class ShopOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_request_id", nullable = false)
    private Long jobRequestId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "price_estimate", nullable = false)
    private BigDecimal priceEstimate;

    @Column(name = "breakdown")
    private String breakdown;

    @Column(name = "parts_tier")
    private String partsTier;

    @Column(name = "estimated_duration_hours")
    private BigDecimal estimatedDurationHours;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
