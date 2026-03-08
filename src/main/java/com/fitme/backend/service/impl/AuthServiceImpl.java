package com.fitme.backend.service.impl;

import com.fitme.backend.config.JwtService;
import com.fitme.backend.dto.*;
import com.fitme.backend.entity.AppUser;
import com.fitme.backend.repository.AppUserRepository;
import com.fitme.backend.service.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthServiceImpl(
      AppUserRepository appUserRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.appUserRepository = appUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthResponseDto signUp(SignUpInput input) {
    if (input.username() == null || input.username().isBlank()) {
      throw new IllegalArgumentException("Username is required");
    }
    if (input.password() == null || input.password().isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (appUserRepository.existsByUsername(input.username())) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (input.email() != null && !input.email().isBlank() && appUserRepository.existsByEmail(input.email())) {
      throw new IllegalArgumentException("Email already exists");
    }

    AppUser savedUser = appUserRepository.save(
        AppUser.builder()
            .username(input.username())
            .password(passwordEncoder.encode(input.password()))
            .email(input.email())
            .fullName(input.fullName())
            .build());

    UserDetails userDetails = toUserDetails(savedUser);
    String token = jwtService.generateToken(userDetails);

    return toAuthResponse(savedUser, token);
  }

  @Override
  public AuthResponseDto logIn(LogInInput input) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(input.username(), input.password()));

    AppUser appUser = appUserRepository.findByUsername(input.username())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    UserDetails userDetails = toUserDetails(appUser);
    String token = jwtService.generateToken(userDetails);

    return toAuthResponse(appUser, token);
  }

  private UserDetails toUserDetails(AppUser appUser) {
    return User.withUsername(appUser.getUsername())
        .password(appUser.getPassword())
        .roles("USER")
        .build();
  }

  private AuthResponseDto toAuthResponse(AppUser appUser, String token) {
    AuthUserDto authUserDto = new AuthUserDto(
        appUser.getId(),
        appUser.getUsername(),
        appUser.getCreatedAt() != null ? appUser.getCreatedAt().toString() : null,
        appUser.getUpdatedAt() != null ? appUser.getUpdatedAt().toString() : null);
    AuthViewerDto viewerDto = new AuthViewerDto(authUserDto, token);
    return new AuthResponseDto(viewerDto);
  }
}
