package com.mealplanner.api.controller;

import com.mealplanner.api.dto.DashboardResponse;
import com.mealplanner.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the dashboard overview endpoint.
 * Returns aggregated statistics and nutritional analytics
 * for the authenticated user's data.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard - Retrieve dashboard summary data.
     * Returns entity counts, calorie breakdown, macro distribution,
     * and daily nutrition data for charts.
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getDashboard(authentication.getName()));
    }
}
