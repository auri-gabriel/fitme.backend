package com.fitme.backend.mappers.impl;

import com.fitme.backend.dto.AuthResponseDto;
import com.fitme.backend.dto.AuthUserDto;
import com.fitme.backend.dto.AuthViewerDto;
import com.fitme.backend.entity.AppUser;
import com.fitme.backend.mappers.AuthMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthMapperImpl implements AuthMapper {

  @Override
  public AuthResponseDto toDto(AppUser appUser, String token) {
    if (appUser == null) {
      return null;
    }

    AuthUserDto authUserDto = new AuthUserDto(
        appUser.getId(),
        appUser.getUsername(),
        appUser.getCreatedAt() != null ? appUser.getCreatedAt().toString() : null,
        appUser.getUpdatedAt() != null ? appUser.getUpdatedAt().toString() : null);

    AuthViewerDto viewerDto = new AuthViewerDto(authUserDto, token);
    return new AuthResponseDto(viewerDto);
  }
}
