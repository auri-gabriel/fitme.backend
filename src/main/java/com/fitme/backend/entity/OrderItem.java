package com.fitme.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false)
  private Long dishId;

  @Column(nullable = false)
  private String dishName;

  @Column(nullable = false)
  private Double unitPrice;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Long restaurantId;

  @Column(nullable = false)
  private String restaurantName;
}
