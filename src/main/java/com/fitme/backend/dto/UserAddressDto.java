package com.fitme.backend.dto;

public record UserAddressDto(
    Long id,
    String label,
    String line1,
    String line2,
    String city,
    String postalCode,
    Boolean isDefault,
    String createdAt,
    String updatedAt) {
}
