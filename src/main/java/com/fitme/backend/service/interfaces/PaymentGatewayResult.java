package com.fitme.backend.service.interfaces;

public record PaymentGatewayResult(
    boolean success,
    String providerTransactionId,
    String message) {
}
