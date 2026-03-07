package com.mealplanner.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ingredient responses with full nutritional data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientResponse {

    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double fiber;
    private Double vitaminA;
    private Double vitaminC;
    private Double calcium;
    private Double iron;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
