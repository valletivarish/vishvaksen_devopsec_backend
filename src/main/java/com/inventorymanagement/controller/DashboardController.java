package com.inventorymanagement.controller;

import com.inventorymanagement.dto.DashboardDto;
import com.inventorymanagement.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the inventory dashboard.
 * Provides a consolidated summary of key inventory metrics for the front-end dashboard view.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Constructor injection for DashboardService dependency.
     *
     * @param dashboardService the service that aggregates dashboard statistics
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves the dashboard summary containing key inventory metrics
     * such as total products, low-stock alerts, recent movements, and category breakdowns.
     *
     * @return the aggregated dashboard data
     */
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboardSummary() {
        DashboardDto dashboard = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(dashboard);
    }
}
