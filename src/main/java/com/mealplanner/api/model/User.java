package com.mealplanner.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * User entity representing registered users of the meal planner application.
 * Stores authentication credentials and profile information.
 * Each user can own recipes, meal plans, shopping lists, and a dietary profile.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique username used for login authentication */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Email address for account recovery and notifications */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCrypt-encoded password hash for secure authentication */
    @Column(nullable = false)
    private String password;

    /** Display name shown on the user's profile */
    @Column(nullable = false, length = 100)
    private String fullName;

    /** Timestamp of when the user account was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last profile update */
    private LocalDateTime updatedAt;

    /** Automatically set creation and update timestamps */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
