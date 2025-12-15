package com.kwikcom.auth.services.impl;

import com.kwikcom.auth.dtos.AuthResponse;
import com.kwikcom.auth.dtos.LoginRequest;
import com.kwikcom.auth.dtos.RegisterRequest;
import com.kwikcom.auth.models.User;
import com.kwikcom.auth.repositories.UserRepository;
import com.kwikcom.auth.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

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
                throw new RuntimeException("User already exists");
            }
            var user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    @Override
    public com.kwikcom.auth.dtos.AuthResponse loginUser(LoginRequest request) {
        try {
            if (!userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Invalid User Credentials");
            }
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid User Credentials");
            }

            var accessToken = jwtService.generateAccessToken(user.getEmail());
            var refreshToken = jwtService.generateRefreshToken(user.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to login user: " + e.getMessage(), e);
        }
    }
}
