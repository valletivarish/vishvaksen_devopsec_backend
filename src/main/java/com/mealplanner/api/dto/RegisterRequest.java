package com.mealplanner.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests with comprehensive validation.
 * Ensures username, email, password, and full name meet minimum requirements.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Username must be 3-50 characters for identification */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /** Email must be a valid email format for account management */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    /** Password must be at least 6 characters for security */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    /** Full name for display on profile and recipes */
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;
}
