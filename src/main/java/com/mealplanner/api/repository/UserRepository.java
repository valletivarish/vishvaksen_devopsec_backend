package com.mealplanner.api.repository;

import com.mealplanner.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom queries for user authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their unique username for login authentication */
    Optional<User> findByUsername(String username);

    /** Find a user by email for password recovery or duplicate checking */
    Optional<User> findByEmail(String email);

    /** Check if a username is already taken during registration */
    boolean existsByUsername(String username);

    /** Check if an email is already registered */
    boolean existsByEmail(String email);
}
