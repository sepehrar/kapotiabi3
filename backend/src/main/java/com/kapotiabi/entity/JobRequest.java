package com.kapotiabi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "job_requests")
public class JobRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "accepts_non_oem")
    private Boolean acceptsNonOem;

    @Column(name = "parts_preference")
    private String partsPreference;

    @Column(name = "search_scope")
    private String searchScope;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "status")
    private String status;

    @Column(name = "offer_deadline")
    private LocalDateTime offerDeadline;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
