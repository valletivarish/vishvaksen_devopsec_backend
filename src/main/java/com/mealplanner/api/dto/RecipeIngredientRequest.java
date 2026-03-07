package com.mealplanner.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specifying an ingredient and its quantity within a recipe.
 * Validates that ingredient ID, quantity, and unit are all provided.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientRequest {

    /** ID of the existing ingredient to add to the recipe */
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    /** Amount of the ingredient needed (must be positive) */
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    /** Measurement unit (e.g., grams, cups, tablespoons) */
    @NotBlank(message = "Unit is required")
    private String unit;
}
