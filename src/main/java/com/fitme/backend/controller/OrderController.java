package com.fitme.backend.controller;

import com.fitme.backend.dto.*;
import com.fitme.backend.service.interfaces.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @MutationMapping
  @PreAuthorize("isAuthenticated()")
  public CreateOrderResponseDto createOrder(@Argument CreateOrderInput input) {
    return orderService.createOrder(input);
  }

  @MutationMapping
  @PreAuthorize("isAuthenticated()")
  public ConfirmPaymentResponseDto confirmPayment(@Argument ConfirmPaymentInput input) {
    return orderService.confirmPayment(input);
  }

  @QueryMapping
  @PreAuthorize("isAuthenticated()")
  public List<OrderDto> getMyOrders() {
    return orderService.getMyOrders();
  }

  @QueryMapping
  @PreAuthorize("isAuthenticated()")
  public OrderDto getMyOrder(@Argument Long id) {
    return orderService.getMyOrder(id);
  }
}
