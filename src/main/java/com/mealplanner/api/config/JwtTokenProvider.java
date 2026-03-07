package com.mealplanner.api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider handles creation and validation of JSON Web Tokens.
 * Uses HMAC-SHA256 for signing tokens with a configurable secret key.
 * Tokens contain the username as subject and have a configurable expiration time.
 */
@Component
public class JwtTokenProvider {

    /** Secret key loaded from application.properties for signing tokens */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Token expiration time in milliseconds (default 24 hours) */
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generates a JWT token for the authenticated user.
     * The token includes the username as subject, issued-at timestamp,
     * and expiration timestamp calculated from the configured duration.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /** Extracts the username (subject) from a valid JWT token */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates the JWT token by verifying its signature and expiration.
     * Returns false for expired, malformed, or tampered tokens.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Creates the HMAC-SHA signing key from the configured secret string */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
