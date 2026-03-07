package com.mealplanner.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating and updating recipes with full input validation.
 * Validates title length, positive time values, serving count range,
 * and nested ingredient list validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    /** Recipe title displayed in search results and listings */
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    /** Brief description of the dish */
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    /** Step-by-step cooking instructions */
    @NotBlank(message = "Instructions are required")
    private String instructions;

    /** Preparation time in minutes before cooking */
    @NotNull(message = "Prep time is required")
    @Min(value = 1, message = "Prep time must be at least 1 minute")
    @Max(value = 1440, message = "Prep time must be at most 1440 minutes")
    private Integer prepTime;

    /** Active cooking time in minutes */
    @NotNull(message = "Cook time is required")
    @Min(value = 0, message = "Cook time must be non-negative")
    @Max(value = 1440, message = "Cook time must be at most 1440 minutes")
    private Integer cookTime;

    /** Number of servings the recipe yields, range 1-100 */
    @NotNull(message = "Servings is required")
    @Min(value = 1, message = "Servings must be at least 1")
    @Max(value = 100, message = "Servings must be at most 100")
    private Integer servings;

    /** Difficulty level: EASY, MEDIUM, or HARD */
    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    /** Optional URL to recipe image */
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;

    /** List of ingredients with quantities for this recipe */
    @Valid
    private List<RecipeIngredientRequest> ingredients;
}
