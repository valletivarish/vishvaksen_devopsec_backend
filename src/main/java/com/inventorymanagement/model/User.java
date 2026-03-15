package com.inventorymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing an authenticated user of the Inventory Management System.
 *
 * Each user is assigned exactly one {@link Role} that governs what operations
 * they may perform. Passwords are expected to be stored in a hashed form;
 * the plain-text minimum-length constraint here validates input before hashing.
 *
 * The {@code username} and {@code email} fields carry unique constraints so that
 * no two accounts can share the same login identifier or contact address.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class User {

    /**
     * Primary key -- auto-generated surrogate identifier for the user record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique login name chosen by the user. Must be between 3 and 50 characters
     * and cannot be blank. Used as the principal identifier during authentication.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Unique email address associated with the account. Must conform to standard
     * email format. Used for notifications and as an alternative login identifier.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Hashed password for the user account. The minimum-length validation
     * applies to the raw input before hashing takes place.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Display name of the user (e.g., "Jane Doe"). Shown in UI elements and
     * audit logs. Limited to 100 characters.
     */
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * Authorization role assigned to this user. Determines the set of
     * permitted actions across the system. Stored as a string in the database
     * for readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * Timestamp recording when the user account was created. Automatically
     * populated by the {@link #onCreate()} callback before the first persist.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

    /**
     * JPA callback that sets the {@code createdAt} timestamp to the current
     * date-time just before the entity is persisted for the first time.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
