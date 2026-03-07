package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specifying a meal slot within a meal plan.
 * Maps a recipe to a specific day of the week and meal type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanEntryRequest {

    /** ID of the recipe to assign to this meal slot */
    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    /** Day of the week (MONDAY, TUESDAY, etc.) */
    @NotBlank(message = "Day of week is required")
    private String dayOfWeek;

    /** Meal type (BREAKFAST, LUNCH, DINNER, SNACK) */
    @NotBlank(message = "Meal type is required")
    private String mealType;
}
