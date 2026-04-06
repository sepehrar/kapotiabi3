package com.kapotiabi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_id", nullable = false, unique = true)
    private Long offerId;

    @Column(name = "job_request_id", nullable = false)
    private Long jobRequestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "confirmed_by_shop")
    private Boolean confirmedByShop;

    @Column(name = "shop_confirmed_at")
    private LocalDateTime shopConfirmedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
