package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses returned after successful login or registration.
 * Contains the JWT token and basic user information for the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT access token for authenticating subsequent API requests */
    private String token;

    /** Type of token (always "Bearer" for JWT) */
    private String type = "Bearer";

    /** Authenticated user's unique identifier */
    private Long userId;

    /** Authenticated user's username */
    private String username;

    /** Authenticated user's full display name */
    private String fullName;

    public AuthResponse(String token, Long userId, String username, String fullName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
    }
}
