package com.mealplanner.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Smart Recipe Meal Planner application.
 * Bootstraps the Spring Boot context with auto-configuration for
 * JPA, Security, Web MVC, and OpenAPI documentation.
 */
@SpringBootApplication
public class MealPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealPlannerApplication.class, args);
    }
}
