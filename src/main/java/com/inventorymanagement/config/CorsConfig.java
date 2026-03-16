package com.inventorymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the Inventory Management System.
 *
 * This configuration allows the frontend application (running on a different origin)
 * to make HTTP requests to this backend API. Without proper CORS headers, browsers
 * would block cross-origin requests due to the same-origin policy.
 *
 * Allowed origins include:
 * - http://localhost:5173 : Vite development server (React frontend)
 * - http://localhost:3000 : Alternative development server port
 *
 * Credentials are allowed so that the frontend can send cookies or authorization
 * headers with cross-origin requests.
 */
@Configuration
public class CorsConfig {

    /**
     * Defines the CORS configuration source that Spring Security will use.
     * This bean is automatically picked up by the .cors() configuration in SecurityConfig.
     *
     * @return the configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins: frontend development servers
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:10002",
                "http://inventory-mgmt-frontend-25173421.s3-website-eu-west-1.amazonaws.com"
        ));

        // Allowed HTTP methods for cross-origin requests
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Allowed request headers; Authorization is needed for JWT, Content-Type for JSON payloads
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        // Allow credentials (cookies, authorization headers) in cross-origin requests
        configuration.setAllowCredentials(true);

        // Cache preflight (OPTIONS) responses for 1 hour to reduce preflight request overhead
        configuration.setMaxAge(3600L);

        // Apply this CORS configuration to all API endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
