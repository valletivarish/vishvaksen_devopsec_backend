package com.inventorymanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test that verifies the Spring application context loads
 * successfully with all beans wired correctly.
 *
 * Uses the "test" profile to activate the H2 in-memory database configuration
 * defined in application-test.properties, avoiding a dependency on an external
 * MySQL instance during test execution.
 */
@SpringBootTest
@ActiveProfiles("test")
class InventoryManagementApplicationTests {

    /**
     * Smoke test -- if this passes, the entire Spring context (controllers,
     * services, repositories, security configuration, etc.) was initialized
     * without errors. A failure here typically points to misconfigured beans,
     * missing dependencies, or incorrect property values.
     */
    @Test
    @DisplayName("Application context loads successfully with test profile")
    void contextLoads() {
        // No explicit assertions needed. The test passes if the context loads
        // without throwing an exception during startup.
    }
}
