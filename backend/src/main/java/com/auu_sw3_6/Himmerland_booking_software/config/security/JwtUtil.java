package com.auu_sw3_6.Himmerland_booking_software.config.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Generate a secure random secret key. The key should be at least 32 bytes for HS256.
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("secret-key123secret-key123secret-key123!".getBytes());

    // Generate a token with claims and the username as the subject
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Create the token with the given claims and subject (username)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 hours
                .signWith(SECRET_KEY, SIG.HS256)
                .compact();
    }

    // Validate the token by checking username and expiration
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
