package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * Validates that both username and password are provided and not empty.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Username for authentication - must not be blank */
    @NotBlank(message = "Username is required")
    private String username;

    /** Password for authentication - must not be blank */
    @NotBlank(message = "Password is required")
    private String password;
}
