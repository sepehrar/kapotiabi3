package com.kapotiabi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class ReviewEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "booking_id", nullable = false, unique = true)
  private Long bookingId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(nullable = false)
  private Integer rating;

  private String comment;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
