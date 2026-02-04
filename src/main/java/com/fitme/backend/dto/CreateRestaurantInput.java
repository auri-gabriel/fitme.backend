package com.fitme.backend.dto;

public record CreateRestaurantInput(
                String name,
                Double rating,
                String location,
                Integer deliveryTime,
                String image) {
}
