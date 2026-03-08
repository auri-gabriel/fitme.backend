package com.fitme.backend.dto;

public record SignUpInput(
    String username,
    String password,
    String email,
    String fullName) {
}
