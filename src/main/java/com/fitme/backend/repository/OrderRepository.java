package com.fitme.backend.repository;

import com.fitme.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> findByAppUserIdAndId(Long appUserId, Long id);

  Optional<Order> findByAppUserIdAndIdempotencyKey(Long appUserId, String idempotencyKey);

  List<Order> findByAppUserIdOrderByCreatedAtDesc(Long appUserId);
}
