package com.inventorymanagement.service;

import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.AuthRequestDto;
import com.inventorymanagement.dto.AuthResponseDto;
import com.inventorymanagement.dto.RegisterRequestDto;
import com.inventorymanagement.exception.DuplicateResourceException;
import com.inventorymanagement.model.Role;
import com.inventorymanagement.model.User;
import com.inventorymanagement.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user authentication and registration.
 *
 * Handles the complete auth lifecycle: creating new accounts with unique
 * credentials, encoding passwords before storage, and issuing JWT tokens
 * upon successful login or registration.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor injection of all dependencies required by the authentication service.
     *
     * @param userRepository        repository for user persistence and lookups
     * @param passwordEncoder       encoder (BCrypt) used to hash passwords before storage
     * @param jwtTokenProvider      utility for generating signed JWT tokens
     * @param authenticationManager Spring Security manager for credential verification
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user in the system.
     *
     * Business rules enforced:
     * 1. Username must be unique -- prevents duplicate login identifiers.
     * 2. Email must be unique -- ensures each account has a distinct contact address.
     * 3. Password is hashed with BCrypt before persistence.
     * 4. Every new user is assigned the default role USER; admins must be promoted separately.
     * 5. A JWT token is generated immediately so the user is logged in upon registration.
     *
     * @param registerRequest the registration payload containing username, email, password, and full name
     * @return an AuthResponseDto containing the JWT token, username, and assigned role
     * @throws DuplicateResourceException if the username or email is already taken
     */
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        // Check username uniqueness to avoid duplicate login identifiers
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("User", "username", registerRequest.getUsername());
        }

        // Check email uniqueness to prevent multiple accounts sharing the same email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("User", "email", registerRequest.getEmail());
        }

        // Build the user entity with hashed password and default USER role
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // Authenticate the newly created user to obtain a valid security context
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        // Generate a JWT token for the authenticated user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Authenticates an existing user and returns a JWT token.
     *
     * The AuthenticationManager delegates to CustomUserDetailsService to load
     * the user from the database and to the PasswordEncoder to verify the
     * supplied password against the stored hash. If authentication fails,
     * Spring Security throws an AuthenticationException upstream.
     *
     * @param authRequest the login payload containing username and password
     * @return an AuthResponseDto containing the JWT token, username, and role
     */
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto authRequest) {
        // Delegate credential verification to Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        // Generate a JWT token from the authenticated principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        // Retrieve the user entity to include the role in the response
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
