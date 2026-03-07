package com.mealplanner.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating and updating dietary profiles with validation.
 * Calorie goals must be between 500 and 10000 per project requirements.
 * Macronutrient goals must be non-negative positive decimals.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDietaryProfileRequest {

    /** Daily calorie target, constrained to realistic range 500-10000 */
    @NotNull(message = "Calorie goal is required")
    @Min(value = 500, message = "Calorie goal must be at least 500")
    @Max(value = 10000, message = "Calorie goal must be at most 10000")
    private Integer calorieGoal;

    /** Daily protein target in grams */
    @NotNull(message = "Protein goal is required")
    @DecimalMin(value = "0.0", message = "Protein goal must be non-negative")
    private Double proteinGoal;

    /** Daily carbohydrate target in grams */
    @NotNull(message = "Carb goal is required")
    @DecimalMin(value = "0.0", message = "Carb goal must be non-negative")
    private Double carbGoal;

    /** Daily fat target in grams */
    @NotNull(message = "Fat goal is required")
    @DecimalMin(value = "0.0", message = "Fat goal must be non-negative")
    private Double fatGoal;

    /** List of food allergies from predefined options */
    private List<String> allergies;

    /** List of dietary restrictions (VEGETARIAN, VEGAN, GLUTEN_FREE, etc.) */
    private List<String> dietaryRestrictions;
}
