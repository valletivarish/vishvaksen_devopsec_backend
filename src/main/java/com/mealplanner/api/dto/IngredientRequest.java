package com.mealplanner.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating and updating ingredients with nutritional data.
 * All nutritional values must be non-negative decimals.
 * Values represent amounts per 100 grams of the ingredient.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {

    /** Unique name of the ingredient */
    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    /** Calories per 100g, must be zero or positive */
    @NotNull(message = "Calories value is required")
    @DecimalMin(value = "0.0", message = "Calories must be non-negative")
    private Double calories;

    /** Protein in grams per 100g */
    @NotNull(message = "Protein value is required")
    @DecimalMin(value = "0.0", message = "Protein must be non-negative")
    private Double protein;

    /** Carbohydrates in grams per 100g */
    @NotNull(message = "Carbs value is required")
    @DecimalMin(value = "0.0", message = "Carbs must be non-negative")
    private Double carbs;

    /** Fat in grams per 100g */
    @NotNull(message = "Fat value is required")
    @DecimalMin(value = "0.0", message = "Fat must be non-negative")
    private Double fat;

    /** Fiber in grams per 100g */
    @NotNull(message = "Fiber value is required")
    @DecimalMin(value = "0.0", message = "Fiber must be non-negative")
    private Double fiber;

    /** Vitamin A in micrograms per 100g */
    @NotNull(message = "Vitamin A value is required")
    @DecimalMin(value = "0.0", message = "Vitamin A must be non-negative")
    private Double vitaminA;

    /** Vitamin C in milligrams per 100g */
    @NotNull(message = "Vitamin C value is required")
    @DecimalMin(value = "0.0", message = "Vitamin C must be non-negative")
    private Double vitaminC;

    /** Calcium in milligrams per 100g */
    @NotNull(message = "Calcium value is required")
    @DecimalMin(value = "0.0", message = "Calcium must be non-negative")
    private Double calcium;

    /** Iron in milligrams per 100g */
    @NotNull(message = "Iron value is required")
    @DecimalMin(value = "0.0", message = "Iron must be non-negative")
    private Double iron;

    /** Standard measurement unit for nutritional reference */
    @NotBlank(message = "Unit is required")
    @Size(max = 50, message = "Unit must be at most 50 characters")
    private String unit;
}
