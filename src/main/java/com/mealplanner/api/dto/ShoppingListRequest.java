package com.mealplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating shopping lists manually or from a meal plan.
 * If mealPlanId is provided, items are auto-generated from the plan's recipes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListRequest {

    /** Name for the shopping list */
    @NotBlank(message = "Shopping list name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    /** Optional meal plan ID to auto-generate items from */
    private Long mealPlanId;
}
