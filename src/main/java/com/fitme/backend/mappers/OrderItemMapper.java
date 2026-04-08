package com.fitme.backend.mappers;

import com.fitme.backend.entity.OrderItem;
import com.fitme.backend.dto.OrderItemDto;

public interface OrderItemMapper {
  OrderItem fromDto(OrderItemDto orderItemDto);

  OrderItemDto toDto(OrderItem orderItem);
}
