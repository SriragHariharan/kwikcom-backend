package com.kwikcom.auth.services;

import com.kwikcom.auth.dtos.AuthResponse;
import com.kwikcom.auth.dtos.LoginRequest;
import com.kwikcom.auth.dtos.RegisterRequest;

public interface AuthService {
    public void registerUser(RegisterRequest request);

    public AuthResponse loginUser(LoginRequest request);
}
