package com.fitme.backend.mappers;

import com.fitme.backend.dto.PaymentAttemptDto;
import com.fitme.backend.entity.PaymentAttempt;

public interface PaymentAttemptMapper {
  PaymentAttempt fromDto(PaymentAttemptDto paymentAttemptDto);

  PaymentAttemptDto toDto(PaymentAttempt paymentAttempt);
}
