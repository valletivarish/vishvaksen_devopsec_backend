package com.mealplanner.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller for deployment smoke testing.
 * Returns a simple JSON response to verify the application is running.
 * This endpoint is publicly accessible (no JWT required).
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /** GET /api/health - Returns application health status */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", "Smart Recipe Meal Planner API"
        ));
    }
}
