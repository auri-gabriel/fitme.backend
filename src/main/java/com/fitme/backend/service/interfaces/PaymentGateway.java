package com.fitme.backend.service.interfaces;

import com.fitme.backend.entity.Order;

public interface PaymentGateway {
  PaymentGatewayResult confirmPayment(Order order, String paymentReference);

  String providerName();
}
