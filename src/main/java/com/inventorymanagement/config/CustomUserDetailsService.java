package com.inventorymanagement.config;

import com.inventorymanagement.model.User;
import com.inventorymanagement.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 *
 * This service bridges the application's User entity (stored in the database)
 * with Spring Security's UserDetails interface. During authentication, Spring
 * Security calls loadUserByUsername() to retrieve user credentials and authorities,
 * which are then compared against the provided login credentials.
 *
 * The User entity is expected to have at minimum:
 * - username (unique identifier for login)
 * - password (BCrypt-hashed)
 * - role (e.g., "ADMIN", "USER" -- prefixed with "ROLE_" for Spring Security)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor injection for the UserRepository dependency.
     *
     * @param userRepository the repository for accessing User entities from the database
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database by their username and converts it to a
     * Spring Security UserDetails object.
     *
     * The user's role is prefixed with "ROLE_" to follow Spring Security's
     * convention for role-based authorization (e.g., hasRole("ADMIN") checks
     * for the authority "ROLE_ADMIN").
     *
     * @param username the username to search for
     * @return a UserDetails object containing the user's credentials and authorities
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user from the database or throw an exception if not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));

        // Map the application User entity to Spring Security's UserDetails
        // The role is prefixed with "ROLE_" to align with Spring Security conventions
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole())
                )
        );
    }
}
