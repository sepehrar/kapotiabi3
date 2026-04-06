package com.kapotiabi.entity;

import jakarta.persistence.*;
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

  @Column(name = "rating", nullable = false)
  private Integer rating;

  @Column(name = "comment")
  private String comment;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
