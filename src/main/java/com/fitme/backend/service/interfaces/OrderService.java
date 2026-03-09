package com.fitme.backend.service.interfaces;

import com.fitme.backend.dto.*;

import java.util.List;

public interface OrderService {
  CreateOrderResponseDto createOrder(CreateOrderInput input);

  ConfirmPaymentResponseDto confirmPayment(ConfirmPaymentInput input);

  List<OrderDto> getMyOrders();

  OrderDto getMyOrder(Long id);
}
