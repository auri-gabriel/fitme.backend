package com.fitme.backend.dto;

public record AuthUserDto(
    Long id,
    String username,
    String createdAt,
    String updatedAt) {
}
