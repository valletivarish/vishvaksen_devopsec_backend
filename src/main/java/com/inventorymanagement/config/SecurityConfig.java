package com.inventorymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 configuration for the Inventory Management System.
 *
 * This configuration establishes a stateless security model suitable for a REST API
 * that uses JWT tokens for authentication. CSRF protection is disabled because the
 * application relies on bearer tokens rather than session cookies, making it immune
 * to CSRF attacks. CORS is enabled to allow the frontend (Vite/React) to communicate
 * with the backend during development and production.
 *
 * Public endpoints (authentication, Swagger docs, health check) are accessible without
 * a token. All other endpoints require a valid JWT in the Authorization header.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Constructor injection ensures immutability and makes dependencies explicit.
     *
     * @param jwtAuthFilter            the filter that extracts and validates JWT tokens
     * @param customUserDetailsService the service that loads user details from the database
     * @param corsConfigurationSource  the CORS configuration source defined in CorsConfig
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService customUserDetailsService,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Defines the security filter chain that applies to all HTTP requests.
     *
     * Key decisions:
     * - CSRF disabled: safe because we use stateless JWT authentication, not cookies.
     * - Session management set to STATELESS: no HTTP session is created or used.
     * - JwtAuthFilter inserted before UsernamePasswordAuthenticationFilter so that
     *   JWT-based authentication is attempted first on every request.
     *
     * @param http the HttpSecurity builder provided by Spring Security
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since this is a stateless REST API using JWT tokens
            .csrf(AbstractHttpConfigurer::disable)

            // Enable CORS using the CorsConfig bean defined in CorsConfig.java
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Configure endpoint authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints that do not require authentication
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/health"
                ).permitAll()
                // All other endpoints require a valid authentication token
                .anyRequest().authenticated()
            )

            // Use stateless session management; no server-side session is maintained
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Set the custom authentication provider that uses our UserDetailsService
            .authenticationProvider(authenticationProvider())

            // Insert the JWT filter before the default username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the authentication provider with our custom UserDetailsService
     * and BCrypt password encoder.
     *
     * @return the configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Provides a BCrypt password encoder for hashing and verifying passwords.
     * BCrypt includes a built-in salt and adaptive cost factor, making it
     * resistant to brute-force attacks.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean so it can be injected into
     * controllers (e.g., the authentication controller for login).
     *
     * @param authenticationConfiguration the Spring-provided authentication configuration
     * @return the AuthenticationManager instance
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
