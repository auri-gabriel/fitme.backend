package com.fitme.backend.mappers;

import com.fitme.backend.entity.Order;
import com.fitme.backend.dto.OrderDto;

public interface OrderMapper {
  Order fromDto(OrderDto orderDto);

  OrderDto toDto(Order order);
}
