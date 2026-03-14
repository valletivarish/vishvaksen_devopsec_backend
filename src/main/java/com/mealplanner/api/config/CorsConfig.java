package com.mealplanner.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration to allow the React frontend to communicate with
 * the Spring Boot backend API. Permits requests from local development
 * (port 5173) and the deployed S3 static website URL.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS filter bean that allows cross-origin requests
     * from the frontend origins with specified HTTP methods and headers.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        /* Allowed frontend origins for local development and production */
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "*"
        ));

        /* Allowed HTTP methods for CRUD operations */
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        /* Allow Authorization header for JWT and Content-Type for JSON payloads */
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
