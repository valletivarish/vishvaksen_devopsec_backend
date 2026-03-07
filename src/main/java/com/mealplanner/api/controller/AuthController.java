package com.mealplanner.api.controller;

import com.mealplanner.api.dto.AuthResponse;
import com.mealplanner.api.dto.LoginRequest;
import com.mealplanner.api.dto.RegisterRequest;
import com.mealplanner.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication endpoints.
 * Handles login and registration with JWT token generation.
 * All endpoints under /api/auth are publicly accessible (no JWT required).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register - Register a new user account.
     * Validates input, checks for duplicates, and returns JWT token.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login - Authenticate an existing user.
     * Validates credentials and returns JWT token for subsequent requests.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
