package com.inventorymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication (login) requests.
 * Carries the user's credentials for verification against stored records.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    /**
     * Username must be between 3 and 50 characters.
     * Blank values are rejected to prevent empty-credential login attempts.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Password must be between 6 and 100 characters.
     * A minimum length of 6 enforces a basic password-strength policy.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
