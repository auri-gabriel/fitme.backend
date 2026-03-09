package com.fitme.backend.dto;

import java.util.List;

public record CreateOrderInput(
    List<CreateOrderItemInput> items,
    String idempotencyKey) {
}
