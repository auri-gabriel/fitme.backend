package com.fitme.backend.dto;

public record CreateOrderItemInput(
    Long dishId,
    Integer quantity) {
}
