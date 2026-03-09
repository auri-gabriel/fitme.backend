package com.fitme.backend.dto;

public record CreateOrderResponseDto(
    OrderDto order,
    PaymentAttemptDto paymentAttempt,
    String message) {
}
