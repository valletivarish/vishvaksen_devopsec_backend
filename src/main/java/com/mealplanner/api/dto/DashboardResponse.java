package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for the dashboard overview containing summary statistics
 * and nutritional data for charts and cards display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    /** Total number of recipes created by the user */
    private long totalRecipes;

    /** Total number of meal plans created by the user */
    private long totalMealPlans;

    /** Total number of ingredients in the system */
    private long totalIngredients;

    /** Total number of shopping lists created by the user */
    private long totalShoppingLists;

    /** Breakdown of calories by meal type for the active meal plan */
    private Map<String, Double> caloriesByMealType;

    /** Macronutrient distribution (protein, carbs, fat) as percentages */
    private Map<String, Double> macroDistribution;

    /** Daily calorie totals for the current meal plan */
    private List<DailyNutrition> dailyNutrition;

    /**
     * Nested DTO for daily nutritional summary within a meal plan.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyNutrition {
        private String day;
        private Double calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }
}
