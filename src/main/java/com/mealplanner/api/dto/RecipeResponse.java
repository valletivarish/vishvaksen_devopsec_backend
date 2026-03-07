package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for recipe responses sent to the frontend.
 * Includes computed nutritional totals per serving and ingredient details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {

    private Long id;
    private String title;
    private String description;
    private String instructions;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String difficulty;
    private String imageUrl;
    private String authorName;
    private Long userId;

    /** Total calories for the entire recipe */
    private Double totalCalories;

    /** Total protein in grams for the entire recipe */
    private Double totalProtein;

    /** Total carbohydrates in grams for the entire recipe */
    private Double totalCarbs;

    /** Total fat in grams for the entire recipe */
    private Double totalFat;

    /** List of ingredients with quantities used in this recipe */
    private List<RecipeIngredientResponse> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested response DTO for individual recipe ingredients.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipeIngredientResponse {
        private Long id;
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private String unit;
        private Double calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }
}
