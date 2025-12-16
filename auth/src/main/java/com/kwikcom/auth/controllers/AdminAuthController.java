package com.kwikcom.auth.controllers;

import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwikcom.auth.dtos.LoginRequest;
import com.kwikcom.auth.services.AuthService;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {
    private final AuthService authService;
    
    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAdmin(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }
        try {
            var response = authService.loginAdmin(request);
            var accessTokenCookie = ResponseCookie.from("accessToken", response.getAccessToken())
                .httpOnly(true)
                .secure(false) // Set to true in production
                .maxAge(60 * 60) // 1 Hour
                .build();
                
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body("Admin verified successfully");
        }
        catch(BadCredentialsException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Something went wrong");
        }
    }
}
