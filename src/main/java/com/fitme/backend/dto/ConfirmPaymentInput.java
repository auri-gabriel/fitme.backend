package com.fitme.backend.dto;

public record ConfirmPaymentInput(
    Long orderId,
    String paymentReference,
    String idempotencyKey) {
}
