package com.maj.TaskMasterApplication.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME_MS = 86400000; // 24 hours

    // 🔐 Generate a JWT token for a given email (used during login)
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key)
                .compact();
    }

    // ✅ Extract email (subject) from token
    public String extractEmail(String token) {
        return parseToken(token).getBody().getSubject();
    }

    // ✅ Validate if token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            parseToken(token); // throws exception if invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 🧪 Internal method to parse token and check signature
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
