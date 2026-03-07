package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for nutritional trend forecast response.
 * Contains predictions for each nutrient type over the forecast period.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastResponse {

    /** Trend direction: INCREASING, DECREASING, or STABLE */
    private String trendDirection;

    /** R-squared value indicating prediction confidence (0-1) */
    private Double confidenceScore;

    /** List of individual nutrient forecasts */
    private List<NutrientForecast> forecasts;

    /**
     * Forecast data for a single nutrient type.
     * Contains historical data points and predicted future values.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutrientForecast {
        /** Name of the nutrient (calories, protein, carbs, fat, etc.) */
        private String nutrientName;
        /** Current average daily intake */
        private Double currentAverage;
        /** Predicted values for the next 7 days */
        private List<Double> predictedValues;
        /** Predicted dates for the forecast period */
        private List<String> predictedDates;
        /** Trend direction for this specific nutrient */
        private String trend;
        /** R-squared confidence for this nutrient's prediction */
        private Double rSquared;
    }
}
