package com.fitme.backend.service.impl;

import com.fitme.backend.entity.Order;
import com.fitme.backend.service.interfaces.PaymentGateway;
import com.fitme.backend.service.interfaces.PaymentGatewayResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "app.payment", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentGateway implements PaymentGateway {

  @Override
  public PaymentGatewayResult confirmPayment(Order order, String paymentReference) {
    if (paymentReference == null || paymentReference.isBlank()) {
      return new PaymentGatewayResult(false, null, "Payment reference is required");
    }

    boolean shouldFail = paymentReference.toUpperCase().contains("FAIL");
    if (shouldFail) {
      return new PaymentGatewayResult(false, "mock-tx-" + UUID.randomUUID(), "Mock payment declined");
    }

    return new PaymentGatewayResult(true, "mock-tx-" + UUID.randomUUID(), "Mock payment approved");
  }

  @Override
  public String providerName() {
    return "mock";
  }
}
