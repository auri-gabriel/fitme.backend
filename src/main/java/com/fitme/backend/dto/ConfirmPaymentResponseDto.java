package com.fitme.backend.dto;

public record ConfirmPaymentResponseDto(
    OrderDto order,
    PaymentAttemptDto paymentAttempt,
    String message) {
}
