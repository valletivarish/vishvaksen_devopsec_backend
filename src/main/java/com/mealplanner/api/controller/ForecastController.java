package com.mealplanner.api.controller;

import com.mealplanner.api.dto.ForecastResponse;
import com.mealplanner.api.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the ML-based nutritional trend forecasting endpoint.
 * Uses Apache Commons Math SimpleRegression to predict future nutrient intake
 * based on historical meal plan data.
 */
@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    /**
     * GET /api/forecast - Generate nutritional trend forecast.
     * Analyzes historical meal plan data and predicts intake for the
     * next 7 days for calories, protein, carbs, and fat.
     */
    @GetMapping
    public ResponseEntity<ForecastResponse> getForecast(Authentication authentication) {
        return ResponseEntity.ok(forecastService.generateForecast(authentication.getName()));
    }
}
