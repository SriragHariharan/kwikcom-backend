package com.kwikcom.auth.services.impl;

import com.kwikcom.auth.dtos.AdminAuthResponse;
import com.kwikcom.auth.dtos.AuthResponse;
import com.kwikcom.auth.dtos.LoginRequest;
import com.kwikcom.auth.dtos.RegisterRequest;
import com.kwikcom.auth.models.User;
import com.kwikcom.auth.repositories.UserRepository;
import com.kwikcom.auth.services.AuthService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Value("${application.security.admin.email}")
    private String adminEmail;

    @Value("${application.security.admin.password}")
    private String adminPassword;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.kwikcom.auth.services.JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            com.kwikcom.auth.services.JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void registerUser(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalStateException("User already exists");
            }
            var user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String role = "ROLE_USER";
        return AuthResponse.builder()
            .accessToken(jwtService.generateAccessToken(user.getEmail(), role))
            .refreshToken(jwtService.generateRefreshToken(user.getEmail(), role))
            .build();
    }

    @Override
    public AdminAuthResponse loginAdmin(LoginRequest request){

        if (!request.getEmail().equals(adminEmail) || !request.getPassword().equals(adminPassword)) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String role = "ROLE_ADMIN";
        return AdminAuthResponse.builder()
            .accessToken(jwtService.generateAccessToken(adminEmail, role))
            .build();
    }
}
