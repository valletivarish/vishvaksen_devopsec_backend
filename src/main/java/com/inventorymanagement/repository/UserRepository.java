package com.inventorymanagement.repository;

import com.inventorymanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 *
 * Provides standard CRUD operations inherited from {@link JpaRepository} as well
 * as custom finder and existence-check methods needed by the authentication and
 * user-management layers of the Inventory Management System.
 *
 * Spring will generate the implementation at runtime; no manual implementation
 * class is required. Services that depend on this repository should receive it
 * via constructor injection.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their unique username.
     *
     * Primarily used during authentication to load the principal by login name.
     *
     * @param username the exact username to search for
     * @return an {@link Optional} containing the matching user, or empty if none exists
     */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves a user by their unique email address.
     *
     * Useful for password-reset flows and alternative login mechanisms that
     * accept an email instead of a username.
     *
     * @param email the exact email address to search for
     * @return an {@link Optional} containing the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given username already exists.
     *
     * Called during registration to enforce username uniqueness before
     * attempting a persist that would violate the database constraint.
     *
     * @param username the username to check
     * @return {@code true} if a user with this username exists, {@code false} otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user with the given email address already exists.
     *
     * Called during registration to enforce email uniqueness before
     * attempting a persist that would violate the database constraint.
     *
     * @param email the email address to check
     * @return {@code true} if a user with this email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);
}
