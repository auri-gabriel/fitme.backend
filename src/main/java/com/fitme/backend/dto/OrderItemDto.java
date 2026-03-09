package com.fitme.backend.dto;

public record OrderItemDto(
    Long id,
    Long dishId,
    String dishName,
    Double unitPrice,
    Integer quantity,
    Long restaurantId,
    String restaurantName,
    Double lineTotal) {
}
