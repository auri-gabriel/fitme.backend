package com.fitme.backend.dto;

public record AuthViewerDto(
    AuthUserDto user,
    String sessionToken) {
}
