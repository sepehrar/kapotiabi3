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
@Table(name = "job_requests")
public class JobRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "car_id", nullable = false)
  private CarEntity car;

  @Column(nullable = false)
  private String description;

  @Column(name = "accepts_non_oem")
  private Boolean acceptsNonOem;

  @Column(name = "parts_preference")
  private String partsPreference;

  @Column(name = "search_scope")
  private String searchScope;

  private String city;
  private String county;

  @Column(name = "ai_summary")
  private String aiSummary;

  private String status;

  @Column(name = "offer_deadline")
  private LocalDateTime offerDeadline;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
