package com.fitme.backend.repository;

import com.fitme.backend.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {
  Optional<PaymentAttempt> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

  Optional<PaymentAttempt> findByOrderIdAndIdempotencyKey(Long orderId, String idempotencyKey);
}
