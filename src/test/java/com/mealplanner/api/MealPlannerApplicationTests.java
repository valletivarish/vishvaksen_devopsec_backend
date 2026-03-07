package com.mealplanner.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test verifying the Spring Boot application context loads correctly.
 * Uses H2 in-memory database via the test profile configuration.
 */
@SpringBootTest
@ActiveProfiles("test")
class MealPlannerApplicationTests {

    @Test
    void contextLoads() {
        /* Verifies all Spring beans are properly configured and the context starts */
    }
}
