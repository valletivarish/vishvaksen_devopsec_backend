package com.mealplanner.api.service;

import com.mealplanner.api.config.JwtTokenProvider;
import com.mealplanner.api.dto.AuthResponse;
import com.mealplanner.api.dto.LoginRequest;
import com.mealplanner.api.dto.RegisterRequest;
import com.mealplanner.api.exception.DuplicateResourceException;
import com.mealplanner.api.model.User;
import com.mealplanner.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService covering registration and login.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_withValidData_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest("newuser", "new@email.com", "password", "New User");
        User savedUser = User.builder().id(1L).username("newuser").fullName("New User").build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken("newuser")).thenReturn("jwt-token");

        AuthResponse result = authService.register(request);

        assertEquals("jwt-token", result.getToken());
        assertEquals("newuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withDuplicateUsername_throwsException() {
        RegisterRequest request = new RegisterRequest("existing", "new@email.com", "password", "User");
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_withDuplicateEmail_throwsException() {
        RegisterRequest request = new RegisterRequest("newuser", "existing@email.com", "password", "User");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest("testuser", "password");
        User user = User.builder().id(1L).username("testuser").fullName("Test User").build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt-token");

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager).authenticate(any());
    }
}
