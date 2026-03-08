package com.fitme.backend.controller;

import com.fitme.backend.dto.AuthResponseDto;
import com.fitme.backend.dto.LogInInput;
import com.fitme.backend.dto.SignUpInput;
import com.fitme.backend.service.interfaces.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @MutationMapping
  public AuthResponseDto signUp(@Argument SignUpInput input) {
    return authService.signUp(input);
  }

  @MutationMapping
  public AuthResponseDto logIn(@Argument LogInInput input) {
    return authService.logIn(input);
  }
}
