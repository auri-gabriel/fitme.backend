package com.fitme.backend.mappers.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fitme.backend.dto.OrderDto;
import com.fitme.backend.dto.OrderItemDto;
import com.fitme.backend.entity.Order;
import com.fitme.backend.entity.OrderItem;
import com.fitme.backend.mappers.OrderItemMapper;
import com.fitme.backend.mappers.OrderMapper;

@Component
public class OrderMapperImpl implements OrderMapper {

  private OrderItemMapper orderItemMapper;

  public OrderMapperImpl(OrderItemMapper orderItemMapper) {
    this.orderItemMapper = orderItemMapper;
  }

  @Override
  public Order fromDto(OrderDto orderDto) {
    if (orderDto == null) {
      return null;
    }

    Order order = Order.builder()
        .id(orderDto.id())
        .status(orderDto.status())
        .totalAmount(orderDto.totalAmount())
        .paymentReference(orderDto.paymentReference())
        .createdAt(parseDateTime(orderDto.createdAt()))
        .updatedAt(parseDateTime(orderDto.updatedAt()))
        .items(new ArrayList<>())
        .build();

    if (orderDto.items() != null) {
      List<OrderItem> orderItems = orderDto.items().stream()
          .map(orderItemMapper::fromDto)
          .peek(orderItem -> orderItem.setOrder(order))
          .toList();

      order.setItems(new ArrayList<>(orderItems));
    }

    return order;
  }

  @Override
  public OrderDto toDto(Order order) {
    List<OrderItemDto> orderItems = order.getItems().stream()
        .map(orderItem -> orderItemMapper.toDto(orderItem))
        .toList();

    return new OrderDto(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getPaymentReference(),
        order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
        order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
        orderItems);
  }

  private LocalDateTime parseDateTime(String value) {
    return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
  }

}
