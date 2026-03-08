package com.fitme.backend.service.interfaces;

import com.fitme.backend.dto.AuthResponseDto;
import com.fitme.backend.dto.LogInInput;
import com.fitme.backend.dto.SignUpInput;

public interface AuthService {
  AuthResponseDto signUp(SignUpInput input);

  AuthResponseDto logIn(LogInInput input);
}
