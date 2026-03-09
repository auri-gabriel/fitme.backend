package com.fitme.backend.dto;

import com.fitme.backend.entity.OrderStatus;

import java.util.List;

public record OrderDto(
    Long id,
    OrderStatus status,
    Double totalAmount,
    String paymentReference,
    String createdAt,
    String updatedAt,
    List<OrderItemDto> items) {
}
