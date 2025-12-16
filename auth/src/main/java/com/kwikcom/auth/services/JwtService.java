package com.kwikcom.auth.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @org.springframework.beans.factory.annotation.Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @org.springframework.beans.factory.annotation.Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @org.springframework.beans.factory.annotation.Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, String role) {
        return buildToken(username, accessTokenExpiration, role);
    }

    public String generateRefreshToken(String username, String role) {
        return buildToken(username, refreshTokenExpiration, role);
    }

    private String buildToken(String username, long expiration , String role) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("role", role)
                .signWith(getSigningKey())
                .compact();
    }
}
