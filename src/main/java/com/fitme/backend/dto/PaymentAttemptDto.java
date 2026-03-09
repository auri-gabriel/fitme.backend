package com.fitme.backend.dto;

import com.fitme.backend.entity.PaymentStatus;

public record PaymentAttemptDto(
    Long id,
    PaymentStatus status,
    String provider,
    String providerTransactionId,
    String paymentReference,
    String message,
    String createdAt) {
}
