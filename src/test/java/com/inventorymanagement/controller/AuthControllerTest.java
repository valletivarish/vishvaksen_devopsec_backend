package com.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.config.CustomUserDetailsService;
import com.inventorymanagement.config.JwtAuthFilter;
import com.inventorymanagement.config.JwtTokenProvider;
import com.inventorymanagement.dto.AuthRequestDto;
import com.inventorymanagement.dto.AuthResponseDto;
import com.inventorymanagement.dto.RegisterRequestDto;
import com.inventorymanagement.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for {@link AuthController}.
 *
 * Uses @WebMvcTest to load only the web layer (controller + exception handlers)
 * with security filters disabled so that authentication is not required for
 * testing the controller logic itself.
 *
 * The AuthService is mocked to isolate controller behavior from business logic.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** Mock the authentication service -- controller delegates all logic here. */
    @MockBean
    private AuthService authService;

    /** Security beans must be mocked even with filters disabled to satisfy DI. */
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    // -----------------------------------------------------------------------
    // POST /api/auth/register
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/register returns 201 with valid registration data")
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto(
                "testuser", "test@example.com", "password123", "Test User");
        AuthResponseDto response = AuthResponseDto.builder()
                .token("jwt-token-123")
                .username("testuser")
                .role("USER")
                .build();

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register returns 400 when username is blank")
    void testRegister_ValidationError() throws Exception {
        // Arrange -- blank username should trigger validation failure
        RegisterRequestDto request = new RegisterRequestDto(
                "", "test@example.com", "password123", "Test User");

        // Act & Assert -- the @Valid annotation on the controller should reject this
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------------------------------------------
    // POST /api/auth/login
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/login returns 200 with valid credentials")
    void testLogin_Success() throws Exception {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("testuser", "password123");
        AuthResponseDto response = AuthResponseDto.builder()
                .token("jwt-token-456")
                .username("testuser")
                .role("USER")
                .build();

        when(authService.login(any(AuthRequestDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-456"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
