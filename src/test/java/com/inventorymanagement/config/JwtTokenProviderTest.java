package com.inventorymanagement.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Use a 256-bit test secret key (Base64-encoded)
        String secret = Base64.getEncoder().encodeToString(
                "ThisIsAVerySecureSecretKeyForTestingPurposesOnly2025!".getBytes());
        jwtTokenProvider = new JwtTokenProvider(secret, 86400000L);

        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("generateToken produces a non-null token")
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("getUsernameFromToken extracts the correct username")
    void testGetUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getExpirationFromToken returns a future date")
    void testGetExpirationFromToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        Date expiration = jwtTokenProvider.getExpirationFromToken(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("validateToken returns true for a valid token and matching user")
    void testValidateToken_Valid() {
        String token = jwtTokenProvider.generateToken(userDetails);
        boolean valid = jwtTokenProvider.validateToken(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("validateToken returns false for a different user")
    void testValidateToken_WrongUser() {
        String token = jwtTokenProvider.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());

        boolean valid = jwtTokenProvider.validateToken(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("validateToken returns false for an expired token")
    void testValidateToken_Expired() {
        // Create provider with 0ms expiration so token is immediately expired
        String secret = Base64.getEncoder().encodeToString(
                "ThisIsAVerySecureSecretKeyForTestingPurposesOnly2025!".getBytes());
        JwtTokenProvider expiredProvider = new JwtTokenProvider(secret, 0L);

        String token = expiredProvider.generateToken(userDetails);
        boolean valid = expiredProvider.validateToken(token, userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("validateToken returns false for a malformed token")
    void testValidateToken_Malformed() {
        boolean valid = jwtTokenProvider.validateToken("not.a.valid.token", userDetails);

        assertThat(valid).isFalse();
    }
}
