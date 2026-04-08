package com.fitme.backend.mappers.impl;

import org.springframework.stereotype.Component;

import com.fitme.backend.dto.OrderItemDto;
import com.fitme.backend.entity.OrderItem;
import com.fitme.backend.mappers.OrderItemMapper;

@Component
public class OrderItemMapperImpl implements OrderItemMapper {

  @Override
  public OrderItem fromDto(OrderItemDto orderItemDto) {
    if (orderItemDto == null) {
      return null;
    }

    return OrderItem.builder()
        .id(orderItemDto.id())
        .dishId(orderItemDto.dishId())
        .dishName(orderItemDto.dishName())
        .unitPrice(orderItemDto.unitPrice())
        .quantity(orderItemDto.quantity())
        .restaurantId(orderItemDto.restaurantId())
        .restaurantName(orderItemDto.restaurantName())
        .build();
  }

  @Override
  public OrderItemDto toDto(OrderItem orderItem) {
    return new OrderItemDto(
        orderItem.getId(),
        orderItem.getDishId(),
        orderItem.getDishName(),
        orderItem.getUnitPrice(),
        orderItem.getQuantity(),
        orderItem.getRestaurantId(),
        orderItem.getRestaurantName(),
        orderItem.getUnitPrice() * orderItem.getQuantity());
  }

}
