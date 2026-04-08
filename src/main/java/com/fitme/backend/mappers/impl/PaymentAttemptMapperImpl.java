package com.fitme.backend.mappers.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fitme.backend.dto.PaymentAttemptDto;
import com.fitme.backend.entity.PaymentAttempt;
import com.fitme.backend.mappers.PaymentAttemptMapper;

@Component
public class PaymentAttemptMapperImpl implements PaymentAttemptMapper {

  @Override
  public PaymentAttempt fromDto(PaymentAttemptDto paymentAttemptDto) {
    if (paymentAttemptDto == null) {
      return null;
    }

    return PaymentAttempt.builder()
        .id(paymentAttemptDto.id())
        .status(paymentAttemptDto.status())
        .provider(paymentAttemptDto.provider())
        .providerTransactionId(paymentAttemptDto.providerTransactionId())
        .paymentReference(paymentAttemptDto.paymentReference())
        .message(paymentAttemptDto.message())
        .createdAt(parseDateTime(paymentAttemptDto.createdAt()))
        .build();
  }

  @Override
  public PaymentAttemptDto toDto(PaymentAttempt paymentAttempt) {
    if (paymentAttempt == null) {
      return null;
    }

    return new PaymentAttemptDto(
        paymentAttempt.getId(),
        paymentAttempt.getStatus(),
        paymentAttempt.getProvider(),
        paymentAttempt.getProviderTransactionId(),
        paymentAttempt.getPaymentReference(),
        paymentAttempt.getMessage(),
        paymentAttempt.getCreatedAt() != null ? paymentAttempt.getCreatedAt().toString() : null);
  }

  private LocalDateTime parseDateTime(String value) {
    return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
  }
}
