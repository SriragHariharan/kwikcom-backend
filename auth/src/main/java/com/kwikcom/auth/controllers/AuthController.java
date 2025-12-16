package com.kwikcom.auth.controllers;

import com.kwikcom.auth.dtos.LoginRequest;
import com.kwikcom.auth.dtos.RegisterRequest;
import com.kwikcom.auth.services.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        try {
            authService.registerUser(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        try {
            var authResponse = authService.loginUser(request);
            System.out.println("AuthResponse: ");
            System.out.println(authResponse);
            var accessTokenCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                    .httpOnly(true)
                    .secure(false) // Set to true in production
                    .maxAge(60 * 60) // 1 Hour
                    .build();

            var refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // Set to true in production
                    .maxAge(60 * 60 * 24 * 7) // 7 Days
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body("User logged in successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during login");
        }
    }
}
