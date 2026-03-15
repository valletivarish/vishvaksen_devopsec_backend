package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after successful authentication.
 * Contains the JWT token and basic user information for the client to store.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    /** JSON Web Token used to authorize subsequent API requests. */
    private String token;

    /** Authenticated user's username, echoed back for client-side display. */
    private String username;

    /** Role assigned to the user (e.g., ADMIN, USER) for client-side access control hints. */
    private String role;
}
