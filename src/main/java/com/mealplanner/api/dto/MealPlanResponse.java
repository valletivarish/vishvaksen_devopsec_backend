package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for meal plan responses with nested meal entries.
 * Includes daily nutritional totals computed from recipe ingredients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;

    /** Total calories across all meals in the plan */
    private Double totalCalories;

    /** Total protein across all meals in the plan */
    private Double totalProtein;

    /** List of individual meal entries with recipe details */
    private List<MealPlanEntryResponse> entries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested response DTO for individual meal plan entries.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MealPlanEntryResponse {
        private Long id;
        private Long recipeId;
        private String recipeTitle;
        private String dayOfWeek;
        private String mealType;
        private Double calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }
}
