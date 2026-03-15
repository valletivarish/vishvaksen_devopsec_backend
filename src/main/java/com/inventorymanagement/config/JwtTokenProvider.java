package com.inventorymanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class responsible for generating, parsing, and validating JSON Web Tokens.
 *
 * JWT Authentication Flow:
 * 1. Client sends login credentials to /api/auth/login.
 * 2. Server authenticates the user and calls generateToken() to create a signed JWT.
 * 3. The JWT is returned to the client in the response body.
 * 4. Client includes the JWT in the Authorization header (Bearer <token>) for subsequent requests.
 * 5. JwtAuthFilter intercepts each request, extracts the token, and calls validateToken()
 *    to verify its signature and expiration before granting access.
 *
 * The token is signed using HMAC-SHA256 with a secret key configured in application.properties.
 * This ensures that tokens cannot be forged or tampered with without the secret.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey signingKey;
    private final long expirationMs;

    /**
     * Constructs the JwtTokenProvider with the secret key and expiration time
     * loaded from application.properties.
     *
     * The secret is Base64-encoded in the properties file and decoded here to
     * produce a 256-bit (or larger) HMAC-SHA key suitable for signing JWTs.
     *
     * @param secret       Base64-encoded secret key from jwt.secret property
     * @param expirationMs token validity duration in milliseconds from jwt.expiration property
     */
    public JwtTokenProvider(
            @Value("${jwt.secret:VGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JJbnZlbnRvcnlNYW5hZ2VtZW50U3lzdGVtMjAyNQ==}")
            String secret,
            @Value("${jwt.expiration:86400000}")
            long expirationMs) {
        // Decode the Base64-encoded secret and create an HMAC-SHA signing key
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT for an authenticated user.
     *
     * The token contains:
     * - Subject: the username (used to identify the user on subsequent requests)
     * - Issued-at timestamp: when the token was created
     * - Expiration timestamp: when the token becomes invalid
     * - Signature: HMAC-SHA256 digest ensuring integrity and authenticity
     *
     * @param userDetails the authenticated user's details
     * @return the compact, URL-safe JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates the JWT with the specified claims and subject.
     *
     * @param claims  additional claims to include in the token payload
     * @param subject the principal (username) that the token identifies
     * @return the signed JWT string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT string
     * @return the username stored in the token's subject claim
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT string
     * @return the expiration date of the token
     */
    public Date getExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract a specific claim from the token using a resolver function.
     *
     * @param token          the JWT string
     * @param claimsResolver a function that extracts the desired claim from the Claims object
     * @param <T>            the type of the claim value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT and returns all claims contained in its payload.
     * This method verifies the token's signature using the signing key.
     *
     * @param token the JWT string
     * @return the Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks whether a token has expired by comparing its expiration date
     * against the current system time.
     *
     * @param token the JWT string
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    /**
     * Validates a JWT token by checking two conditions:
     * 1. The username in the token matches the provided UserDetails username.
     * 2. The token has not expired.
     *
     * If the token is malformed, expired, or has an invalid signature,
     * the exception is caught, logged, and false is returned.
     *
     * @param token       the JWT string to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid for the given user, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
