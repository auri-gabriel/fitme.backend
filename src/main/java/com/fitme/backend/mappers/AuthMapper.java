package com.fitme.backend.mappers;

import com.fitme.backend.dto.AuthResponseDto;
import com.fitme.backend.entity.AppUser;

public interface AuthMapper {
  AuthResponseDto toDto(AppUser appUser, String token);
}
