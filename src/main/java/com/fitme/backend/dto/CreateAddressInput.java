package com.fitme.backend.dto;

public record CreateAddressInput(
    String label,
    String line1,
    String line2,
    String city,
    String postalCode,
    Boolean isDefault) {
}
