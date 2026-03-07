package com.mealplanner.api.service;

import com.mealplanner.api.config.JwtTokenProvider;
import com.mealplanner.api.dto.AuthResponse;
import com.mealplanner.api.dto.LoginRequest;
import com.mealplanner.api.dto.RegisterRequest;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user authentication and registration.
 * Manages JWT token generation, password encoding, and duplicate checking.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user after checking for duplicate username and email.
     * Password is hashed with BCrypt before storage.
     * Returns JWT token and user info for immediate login after registration.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        /* Check for existing username to prevent duplicates */
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }

        /* Check for existing email to prevent duplicate accounts */
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        /* Create new user entity with BCrypt-encoded password */
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        User savedUser = userRepository.save(user);

        /* Generate JWT token for the newly registered user */
        String token = jwtTokenProvider.generateToken(savedUser.getUsername());
        return new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getFullName());
    }

    /**
     * Authenticates a user with username and password.
     * Uses Spring Security's AuthenticationManager to verify credentials.
     * Returns JWT token on successful authentication.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        /* Authenticate credentials through Spring Security */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        /* Retrieve user details for response */
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        /* Generate JWT token for the authenticated user */
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName());
    }
}
