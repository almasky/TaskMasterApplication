package com.maj.TaskMasterApplication.security;

import com.maj.TaskMasterApplication.model.User; // If you extract roles or other claims from User entity
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationInMs;

    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        // Ensure the secret key is strong enough for HS512
        // A common way is to ensure it's a base64 encoded string of sufficient length
        // For simplicity, if your jwtSecretString is not already base64 or of sufficient length,
        // this might throw an error. Consider using a robust key generation/management strategy.
        // For demonstration, we'll derive a key. Ensure your actual secret is secure and configured properly.
        if (jwtSecretString == null || jwtSecretString.length() < 64) { // HS512 needs 512 bits / 64 bytes
            logger.warn("JWT secret is not configured or too short. Using a default (unsafe) key for demonstration.");
            jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Generates a secure random key
        } else {
            // If jwtSecretString is a raw string, convert to bytes.
            // If it's base64 encoded, decode it first.
            jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal(); // UserDetails from Spring Security

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Extract roles for claims
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // If your UserDetails is your custom User entity, you can get the ID directly
        Long userId = null;
        if (userPrincipal instanceof com.maj.TaskMasterApplication.model.User) {
            userId = ((com.maj.TaskMasterApplication.model.User) userPrincipal).getId();
        }


        Claims claims = Jwts.claims().setSubject(userPrincipal.getUsername());
        claims.put("roles", roles);
        if (userId != null) {
            claims.put("userId", userId);
        }


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }
    // Overloaded method to generate token directly from User object (e.g. after registration)
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("roles", roles);
        claims.put("userId", user.getId());


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class); // Get custom claim
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}